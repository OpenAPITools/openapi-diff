package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.compare.OpenApiDiffOptions;

public class BackwardCompatibilityTest {
  private final String OPENAPI_DOC1 = "backwardCompatibility/bc_1.yaml";
  private final String OPENAPI_DOC2 = "backwardCompatibility/bc_2.yaml";
  private final String OPENAPI_DOC3 = "backwardCompatibility/bc_3.yaml";
  private final String OPENAPI_DOC4 = "backwardCompatibility/bc_4.yaml";
  private final String OPENAPI_DOC5 = "backwardCompatibility/bc_5.yaml";
  private final String OPENAPI_DOC_ENUM_BASE = "backwardCompatibility/bc_enum_base.yaml";
  private final String OPENAPI_DOC_ENUM_REQ_ADDED = "backwardCompatibility/bc_enum_req_added.yaml";
  private final String OPENAPI_DOC_ENUM_RESP_ADDED =
      "backwardCompatibility/bc_enum_resp_added.yaml";

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

  @Test
  public void testEnumRequestValuesAdded() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC_ENUM_BASE, OPENAPI_DOC_ENUM_REQ_ADDED, true);
  }

  @Test
  public void testEnumRequestValuesRemoved() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC_ENUM_REQ_ADDED, OPENAPI_DOC_ENUM_BASE);
  }

  @Test
  public void testEnumResponseValuesAdded_lenient() {
    OpenApiDiffOptions options =
        OpenApiDiffOptions.builder().allowResponseEnumAdditions(true).build();
    assertOpenApiBackwardCompatible(OPENAPI_DOC_ENUM_BASE, OPENAPI_DOC_ENUM_RESP_ADDED, options);
  }

  @Test
  public void testEnumResponseValuesAdded_strict() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC_ENUM_BASE, OPENAPI_DOC_ENUM_RESP_ADDED);
  }

  @Test
  public void testEnumResponseValuesRemoved() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC_ENUM_RESP_ADDED, OPENAPI_DOC_ENUM_BASE, true);
  }
}
