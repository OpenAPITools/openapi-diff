package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiChangedEndpoints;

import org.junit.jupiter.api.Test;

public class RequestDiffTest {
  private final String OPENAPI_DOC1 = "request_diff_1.yaml";
  private final String OPENAPI_DOC2 = "request_diff_2.yaml";

  @Test
  public void testDiffDifferent() {
    assertOpenApiChangedEndpoints(OPENAPI_DOC1, OPENAPI_DOC2);
  }
}
