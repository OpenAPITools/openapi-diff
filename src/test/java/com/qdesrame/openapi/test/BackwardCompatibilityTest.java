package com.qdesrame.openapi.test;

import static com.qdesrame.openapi.test.TestUtils.assertOpenApiBackwardCompatible;
import static com.qdesrame.openapi.test.TestUtils.assertOpenApiBackwardIncompatible;

import org.junit.jupiter.api.Test;

/** Created by adarsh.sharma on 24/12/17. */
public class BackwardCompatibilityTest {
  private final String OPENAPI_DOC1 = "backwardCompatibility/bc_1.yaml";
  private final String OPENAPI_DOC2 = "backwardCompatibility/bc_2.yaml";
  private final String OPENAPI_DOC3 = "backwardCompatibility/bc_3.yaml";
  private final String OPENAPI_DOC4 = "backwardCompatibility/bc_4.yaml";
  private final String OPENAPI_DOC5 = "backwardCompatibility/bc_5.yaml";

  @Test
  public void testNoChange() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC1, OPENAPI_DOC1, false);
  }

  @Test
  public void testApiAdded() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC1, OPENAPI_DOC2, true);
  }

  @Test
  public void testApiMissing() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC2, OPENAPI_DOC1);
  }

  @Test
  public void testApiChangedOperationAdded() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC2, OPENAPI_DOC3, true);
  }

  @Test
  public void testApiChangedOperationMissing() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC3, OPENAPI_DOC2);
  }

  @Test
  public void testApiOperationChanged() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC2, OPENAPI_DOC4, true);
  }

  @Test
  public void testApiReadWriteOnlyPropertiesChanged() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC1, OPENAPI_DOC5, true);
  }
}
