package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

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
  public void testAllOfArrayToDirectArrayIsCompatible() {
    ChangedOpenApi diff = OpenApiCompare.fromLocations(ALLOF_ARRAY, DIRECT_ARRAY);
    assertThat(diff.isCompatible()).isTrue();
  }
}
