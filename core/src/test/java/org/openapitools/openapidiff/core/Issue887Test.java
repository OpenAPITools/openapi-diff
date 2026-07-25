package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;

import org.junit.jupiter.api.Test;

public class Issue887Test {

  private final String ALLOF_ARRAY = "issue-887-1.yaml";
  private final String DIRECT_ARRAY = "issue-887-2.yaml";

  @Test
  public void testAllOfArrayToDirectArrayDoesNotThrow() {
    assertThatCode(() -> OpenApiCompare.fromLocations(ALLOF_ARRAY, DIRECT_ARRAY))
        .doesNotThrowAnyException();
  }

  @Test
  public void testDirectArrayToAllOfArrayDoesNotThrow() {
    assertThatCode(() -> OpenApiCompare.fromLocations(DIRECT_ARRAY, ALLOF_ARRAY))
        .doesNotThrowAnyException();
  }

  @Test
  public void testAllOfArrayToDirectArrayAreEquals() {
    assertOpenApiAreEquals(ALLOF_ARRAY, DIRECT_ARRAY);
  }
}
