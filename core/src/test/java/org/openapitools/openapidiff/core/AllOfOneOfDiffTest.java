package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class AllOfOneOfDiffTest {
  @Test
  void allOfReferringToOneOfSchemasAreSupported() {
    ChangedOpenApi diff = OpenApiCompare.fromLocations("issue-317_1.json", "issue-317_2.json");
    assertThat(diff.isCoreChanged().isUnchanged()).isTrue();
  }
}
