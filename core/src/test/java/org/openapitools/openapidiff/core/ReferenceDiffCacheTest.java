package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;

import org.junit.jupiter.api.Test;

public class ReferenceDiffCacheTest {

  private final String OPENAPI_DOC1 = "schema_diff_cache_1.yaml";

  @Test
  public void testDiffSame() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
  }
}
