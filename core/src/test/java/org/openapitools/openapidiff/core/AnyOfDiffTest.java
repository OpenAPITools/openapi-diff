package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiChangedEndpoints;

import org.junit.jupiter.api.Test;

public class AnyOfDiffTest {

  private final String OPENAPI_DOC1 = "anyOf_diff_1.yaml";
  private final String OPENAPI_DOC2 = "anyOf_diff_2.yaml";
  private final String OPENAPI_DOC3 = "anyOf_diff_3.yaml";
  private final String OPENAPI_DOC4 = "anyOf_diff_4.yaml";

  @Test
  public void testDiffSame() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
  }

  @Test
  public void testDiffSameWithAnyOf() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC2);
  }

  @Test
  public void testDiffDifferent1() {
    assertOpenApiChangedEndpoints(OPENAPI_DOC1, OPENAPI_DOC3);
  }

  @Test
  public void testDiffDifferent2() {
    assertOpenApiChangedEndpoints(OPENAPI_DOC1, OPENAPI_DOC4);
  }
}
