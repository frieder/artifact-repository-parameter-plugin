package io.jenkins.plugins.artifactrepo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** An immutable bean to store the return code and entity payload of an HTTP request. */
@AllArgsConstructor
@Getter
public class HttpResponse {
  private final int rc;
  private final String payload;
}
