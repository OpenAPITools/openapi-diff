package com.qdesrame.openapi.test;

import static com.qdesrame.openapi.test.TestUtils.assertOpenApiAreEquals;
import static com.qdesrame.openapi.test.TestUtils.assertOpenApiBackwardIncompatible;
import static com.qdesrame.openapi.test.TestUtils.assertOpenApiChangedEndpoints;

import org.junit.jupiter.api.Test;

/** Created by adarsh.sharma on 19/12/17. */
public class OneOfDiffTest {

  private final String OPENAPI_DOC1 = "oneOf_diff_1.yaml";
  private final String OPENAPI_DOC2 = "oneOf_diff_2.yaml";
  private final String OPENAPI_DOC3 = "oneOf_diff_3.yaml";
  private final String OPENAPI_DOC4 = "composed_schema_1.yaml";
  private final String OPENAPI_DOC5 = "composed_schema_2.yaml";
  private final String OPENAPI_DOC6 = "oneOf_discriminator-changed_1.yaml";
  private final String OPENAPI_DOC7 = "oneOf_discriminator-changed_2.yaml";
  private final String OPENAPI_DOC8 = "oneOf_discriminator-missing_1.yaml";
  private final String OPENAPI_DOC9 = "oneOf_discriminator-missing_2.yaml";

  @Test
  public void testDiffSame() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
  }

  @Test
  public void testDiffDifferentMapping() {
    assertOpenApiChangedEndpoints(OPENAPI_DOC1, OPENAPI_DOC2);
  }

  @Test
  public void testDiffSameWithOneOf() {
    assertOpenApiAreEquals(OPENAPI_DOC2, OPENAPI_DOC3);
  }

  @Test
  public void testComposedSchema() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC4, OPENAPI_DOC5);
  }

  @Test
  public void testOneOfDiscrimitatorChanged() {
    // The oneOf 'discriminator' changed: 'realtype' -> 'othertype':
    assertOpenApiBackwardIncompatible(OPENAPI_DOC6, OPENAPI_DOC7);
  }

  @Test
  public void testOneOfDiscrimitatorMissingSameOrder() {
    assertOpenApiAreEquals(OPENAPI_DOC8, OPENAPI_DOC8);
  }

  @Test
  public void testOneOfDiscrimitatorMissingDifferentOrder() {
    assertOpenApiAreEquals(OPENAPI_DOC8, OPENAPI_DOC9);
  }
}
