package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.output.ConsoleRender;

class AdditionalPropertiesTest {
  @Test
  void booleanAdditionalPropertiesAreSupported() {
    ChangedOpenApi diff = OpenApiCompare.fromLocations("issue-256_1.json", "issue-256_2.json");
    ConsoleRender render = new ConsoleRender();
    assertThat(render.render(diff)).isNotBlank();
  }
}
