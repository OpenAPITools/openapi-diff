package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiChangedEndpoints;

import org.junit.jupiter.api.Test;

public class ParameterDiffTest {
  private final String OPENAPI_DOC1 = "parameters_diff_1.yaml";
  private final String OPENAPI_DOC2 = "parameters_diff_2.yaml";

  @Test
  public void testDiffDifferent() {
    assertOpenApiChangedEndpoints(OPENAPI_DOC1, OPENAPI_DOC2);
  }
}
