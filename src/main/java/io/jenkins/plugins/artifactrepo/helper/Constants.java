package io.jenkins.plugins.artifactrepo.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

  public static final String PLUGIN_NAME = "Artifact Repository Parameter";

  /** Defines the ID of the different parameter options. */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class ParameterType {
    public static final String PATH = "path";
    public static final String VERSION = "version";
    public static final String REPOSITORY = "repository";
    public static final String TEST = "test";
  }

  /**
   * Defines constants for the HTTP requests.
   */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class HTTP {
    public static final String AGENT = "Jenkins Plugin - Artifact Repository Parameter";
    public static final int CONN_TIMEOUT = 60000;
    public static final int SOCKET_TIMEOUT = 20000;
    public static final boolean FOLLOW_REDIRECT = true;
    public static final int MAX_REDIRECTS = 10;
  }
}
