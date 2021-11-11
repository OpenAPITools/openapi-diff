package org.openapitools.openapidiff.maven;

import org.apache.maven.plugin.MojoFailureException;

class BackwardIncompatibilityException extends MojoFailureException {
  public BackwardIncompatibilityException(String message) {
    super(message);
  }
}
