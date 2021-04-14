package org.openapitools.openapidiff.core;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;

import org.junit.jupiter.api.Test;

public class PathDiffTest {

  private final String OPENAPI_PATH1 = "path_1.yaml";
  private final String OPENAPI_PATH2 = "path_2.yaml";
  private final String OPENAPI_PATH3 = "path_3.yaml";

  @Test
  public void testEqual() {
    assertOpenApiAreEquals(OPENAPI_PATH1, OPENAPI_PATH2);
  }

  @Test
  public void testMultiplePathWithSameSignature() {
    assertThrows(
        IllegalArgumentException.class, () -> assertOpenApiAreEquals(OPENAPI_PATH3, OPENAPI_PATH3));
  }

}
