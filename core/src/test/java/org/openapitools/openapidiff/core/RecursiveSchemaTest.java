package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;

import org.junit.jupiter.api.Test;

public class RecursiveSchemaTest {

  private final String OPENAPI_DOC1 = "recursive_model_1.yaml";
  private final String OPENAPI_DOC2 = "recursive_model_2.yaml";
  private final String OPENAPI_DOC3 = "recursive_model_3.yaml";
  private final String OPENAPI_DOC4 = "recursive_allof_model_1.yaml";
  private final String OPENAPI_DOC5 = "recursive_allof_model_2.yaml";
  private final String OPENAPI_DOC6 = "recursive_allof_model_3.yaml";

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

  @Test
  public void testDiffSameWithAllOfWrappedCycle() {
    assertOpenApiAreEquals(OPENAPI_DOC4, OPENAPI_DOC4);
  }

  @Test
  public void testDiffChangedInsideAllOfWrappedCycle() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC4, OPENAPI_DOC5);
  }

  @Test
  public void testDiffAllOfWrappedAgainstBareCycle() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC4, OPENAPI_DOC6, true);
  }
}
