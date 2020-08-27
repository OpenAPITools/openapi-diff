package com.qdesrame.openapi.test;

import static com.qdesrame.openapi.test.TestUtils.assertOpenApiAreEquals;

import org.junit.jupiter.api.Test;

/** Created by adarsh.sharma on 25/12/17. */
public class ReferenceDiffCacheTest {

  private final String OPENAPI_DOC1 = "schema_diff_cache_1.yaml";

  @Test
  public void testDiffSame() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
  }
}
