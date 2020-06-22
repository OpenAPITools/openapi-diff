package com.qdesrame.openapi.test;

import static com.qdesrame.openapi.test.TestUtils.assertOpenApiAreEquals;
import static com.qdesrame.openapi.test.TestUtils.assertOpenApiBackwardIncompatible;

import org.junit.jupiter.api.Test;

/** Created by trohrberg on 23/03/19. */
public class AddPropPutDiffTest {
  private final String OPENAPI_DOC1 = "add-prop-put-1.yaml";
  private final String OPENAPI_DOC2 = "add-prop-put-2.yaml";

  @Test
  public void testDiffSame() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
  }

  @Test
  public void testDiffDifferent() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC1, OPENAPI_DOC2);
  }
}
