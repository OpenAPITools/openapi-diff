package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;

import org.junit.jupiter.api.Test;

public class Issue887Test {

  private final String ALLOF_ARRAY = "issue-887-1.yaml";
  private final String DIRECT_ARRAY = "issue-887-2.yaml";

  @Test
  public void testAllOfArrayToDirectArrayAreEquals() {
    assertOpenApiAreEquals(ALLOF_ARRAY, DIRECT_ARRAY);
  }

  @Test
  public void testDirectArrayToAllOfArrayAreEquals() {
    assertOpenApiAreEquals(DIRECT_ARRAY, ALLOF_ARRAY);
  }
}
