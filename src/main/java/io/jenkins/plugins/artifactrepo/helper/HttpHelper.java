package io.jenkins.plugins.artifactrepo.helper;

import hudson.ProxyConfiguration;
import io.jenkins.plugins.artifactrepo.Messages;
import io.jenkins.plugins.artifactrepo.model.HttpProxy;
import io.jenkins.plugins.artifactrepo.model.HttpResponse;
import jenkins.model.Jenkins;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import javax.annotation.Nonnull;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * A simple utility class to help create the HTTP connection from the plugin to the target
 * repository instances.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpHelper {

  /**
   * A generic implementation to do GET requests with automatic resource clean up.
   *
   * @param url The URL to call
   * @param builder The builder object used to create the {@link org.apache.http.client.HttpClient}.
   * @param context A possible context object to add to the request. Can be used to perform
   *     preemptive authentication (required by Nexus).
   * @return An instance of {@link HttpResponse} with both return code and response payload.
   */
  public static HttpResponse get(String url, HttpClientBuilder builder, HttpClientContext context) {
    Validate.notBlank(url, Messages.log_blankUrl());

    try (CloseableHttpClient httpClient = builder.build()) {
      HttpGet get = new HttpGet(url);
      try (CloseableHttpResponse response = httpClient.execute(get, context)) {
        String payload =
            IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
        int rc = response.getStatusLine().getStatusCode();
        return new HttpResponse(rc, payload);
      }
    } catch (IOException e) {
      throw new IllegalArgumentException(Messages.log_requestException(), e);
    }
  }

  /**
   * Convenience method of {@link HttpHelper#get(String, HttpClientBuilder, HttpClientContext)} with
   * a default client context.
   */
  public static HttpResponse get(@Nonnull String url, @Nonnull HttpClientBuilder builder) {
    return get(url, builder, HttpClientContext.create());
  }

  /**
   * Returns an opinionated and preconfigured HttpClient builder object.
   *
   * @param repoCredId The ID of the credentials object that should be used to authenticate at the
   *     target repository instance.
   * @param proxy A proxy object with all the proxy information in it. Can be null.
   * @param ignoreSSL Whether or not to ignore invalid SSL certificates (e. g. self-signed).
   * @return An {@link HttpClientBuilder} object with some pre-defined configurations.
   */
  public static HttpClientBuilder getBuilder(
      String repoCredId, HttpProxy proxy, boolean ignoreSSL) {
    return Optional.of(HttpClients.custom())
        .map(builder -> addDefaultConfig(builder))
        .map(builder -> addBasicAuth(builder, repoCredId, proxy))
        .map(builder -> addProxy(builder, proxy))
        .map(builder -> addSslHandling(builder, ignoreSSL))
        .orElse(HttpClients.custom());
  }

  /** Takes a URL string and creates a {@link HttpHost} object of it. */
  public static HttpHost getHttpHostFromUrl(@Nonnull String urlString)
      throws MalformedURLException {
    URL url = new URL(urlString);
    return new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
  }

  /** Adds a default request configuration that includes user agent, redirect and timeout config. */
  private static HttpClientBuilder addDefaultConfig(HttpClientBuilder builder) {
    RequestConfig.Builder configBuilder =
        RequestConfig.copy(RequestConfig.DEFAULT)
            .setSocketTimeout(Constants.HTTP.SOCKET_TIMEOUT)
            .setConnectionRequestTimeout(Constants.HTTP.CONN_TIMEOUT)
            .setRedirectsEnabled(Constants.HTTP.FOLLOW_REDIRECT)
            .setMaxRedirects(Constants.HTTP.MAX_REDIRECTS);

    return builder
        .setUserAgent(Constants.HTTP.AGENT)
        .setDefaultRequestConfig(configBuilder.build());
  }

  /**
   * Retrieve Jenkins credentials identified by the credential IDs and add them to the HTTP
   * credentials provider.
   */
  private static HttpClientBuilder addBasicAuth(
      HttpClientBuilder builder, String repoCredId, HttpProxy proxy) {
    CredentialsProvider httpProvider = new BasicCredentialsProvider();

    Optional.ofNullable(repoCredId)
        .filter(StringUtils::isNotBlank)
        .map(PluginHelper::getCredentials)
        .ifPresent(
            repoCred ->
                httpProvider.setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(
                        repoCred.getUsername(), repoCred.getPassword().getPlainText())));

    if (!HttpProxy.isValid(proxy) || StringUtils.isBlank(proxy.getCredentialsId())) {
      return builder.setDefaultCredentialsProvider(httpProvider);
    }

    // potential NPE here isn't handled on purpose since we cannot recover if it fails anyways
    Optional.of(PluginHelper.getCredentials(proxy.getCredentialsId()))
        .ifPresent(
            proxyCredId ->
                httpProvider.setCredentials(
                    new AuthScope(proxy.getHost(), proxy.getPort()),
                    new UsernamePasswordCredentials(
                        proxyCredId.getUsername(), proxyCredId.getPassword().getPlainText())));

    return builder.setDefaultCredentialsProvider(httpProvider);
  }

  // TODO add noProxyHost check
  // TODO replace custom HttpProxy class with Jenkins ProxyConfiguration class
  /**
   * Check if either in the build config or globally a proxy is configured and if so add it to the
   * builder.
   */
  private static HttpClientBuilder addProxy(HttpClientBuilder builder, HttpProxy proxy) {
    ProxyConfiguration jenkinsProxy = Jenkins.get().proxy;
    HttpHost proxyHost = null;

    if (HttpProxy.isValid(proxy)) {
      proxyHost = new HttpHost(proxy.getHost(), proxy.getPort(), proxy.getProtocol());
    } else if (jenkinsProxy != null && StringUtils.isNotBlank(jenkinsProxy.name)) {
      proxyHost = new HttpHost(jenkinsProxy.name, jenkinsProxy.port);
    }

    return builder.setProxy(proxyHost);
  }

  /**
   * Ignore invalid (e.g. self-signed) SSL certificates if configured.
   */
  private static HttpClientBuilder addSslHandling(HttpClientBuilder builder, boolean ignoreSSL) {
    if (!ignoreSSL) {
      return builder;
    }

    try {
      SSLContext sslContext =
          new SSLContextBuilder().loadTrustMaterial(null, (x509Certificates, s) -> true).build();
      builder.setSSLContext(sslContext);

      HostnameVerifier verifier = NoopHostnameVerifier.INSTANCE;
      builder.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext, verifier));
    } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
      throw new IllegalArgumentException(Messages.log_errorCertsProcessing(), e);
    }

    return builder;
  }
}
