package io.jenkins.plugins.artifactrepo.model;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import hudson.util.Secret;
import io.jenkins.plugins.artifactrepo.helper.PluginHelper;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * An immutable bean storing settings for a local HTTP proxy. A local proxy can be configured either
 * in the build's configuration or for a server entry in the global config.
 */
@Getter
public final class HttpProxy implements Serializable {
  public static final long serialVersionUID = -2264442474142821023L;

  public static final HttpProxy DISABLED = new HttpProxy("", "", 0, "");
  private final boolean valid;
  private final String protocol;
  private final String host;
  private final int port;
  private final String credentialsId;
  private final String username;
  private final Secret secret;

  // TODO change config.jelly, make port an optional number field, default 0
  @DataBoundConstructor
  public HttpProxy(
      String proxyProtocol, String proxyHost, int proxyPort, String proxyCredentialsId) {
    this.protocol = Optional.ofNullable(proxyProtocol).orElse("HTTPS");
    this.host = proxyHost;
    this.port = proxyPort;
    this.credentialsId = proxyCredentialsId; // TODO check if this must be stored for jelly.config
    StandardUsernamePasswordCredentials credentials = PluginHelper.getCredentials(credentialsId);
    this.username = credentials.getUsername();
    this.secret = credentials.getPassword();

    valid = Stream.of(proxyProtocol, proxyHost).allMatch(StringUtils::isNotBlank);
  }

  // TODO custom constructor

  public static boolean isValid(HttpProxy proxy) {
    return proxy != null && proxy.isValid();
  }

}
