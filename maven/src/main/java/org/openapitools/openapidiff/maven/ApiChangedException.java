package org.openapitools.openapidiff.maven;

import org.apache.maven.plugin.MojoFailureException;

public class ApiChangedException extends MojoFailureException {
  public ApiChangedException(String message) {
    super(message);
  }
}
