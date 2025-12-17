package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;

import org.junit.jupiter.api.Test;

public class RemoteRefsDiffTest {

  private final String OPENAPI_DOC1 = "remoteRefs/diff_1.yaml";
  private final String OPENAPI_DOC2 = "remoteRefs/diff_2.yaml";

  @Test
  public void testDiffSame() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
  }

  @Test
  public void testDiffSameWithAllOf() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC2);
  }
}
