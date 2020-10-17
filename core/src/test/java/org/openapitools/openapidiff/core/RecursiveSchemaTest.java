package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;

import org.junit.jupiter.api.Test;

/** Created by adarsh.sharma on 13/02/18. */
public class RecursiveSchemaTest {

  private final String OPENAPI_DOC1 = "recursive_model_1.yaml";
  private final String OPENAPI_DOC2 = "recursive_model_2.yaml";

  @Test
  public void testDiffSame() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
  }

  @Test
  public void testDiffDifferent() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC1, OPENAPI_DOC2);
  }
}
