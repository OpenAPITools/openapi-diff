package org.openapitools.openapidiff.core;

import org.junit.jupiter.api.Test;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardCompatible;

class EnumDiffTest {

  private final String OPENAPI_PATH1 = "enum_1.yaml";
  private final String OPENAPI_PATH2 = "enum_2.yaml";

  @Test
  public void testDiffSame() {
    assertOpenApiAreEquals(OPENAPI_PATH1, OPENAPI_PATH1);
  }

  @Test
  void appendedEnumsAreNotBreakingChanges() {
    assertOpenApiBackwardCompatible(OPENAPI_PATH1, OPENAPI_PATH2, false);
  }
}
