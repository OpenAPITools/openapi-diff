package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;

import org.junit.jupiter.api.Test;

public class RecursiveSchemaTest {

  private final String OPENAPI_DOC1 = "recursive_model_1.yaml";
  private final String OPENAPI_DOC2 = "recursive_model_2.yaml";
  private final String OPENAPI_DOC3 = "recursive_model_3.yaml";

  @Test
  public void testDiffSame() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
  }

  @Test
  public void testDiffDifferentCyclic() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC1, OPENAPI_DOC3);
  }

  @Test
  public void testDiffDifferent() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC1, OPENAPI_DOC2);
  }
}
