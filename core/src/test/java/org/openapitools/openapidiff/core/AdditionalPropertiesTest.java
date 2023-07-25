package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.output.ConsoleRender;

class AdditionalPropertiesTest {
  @Test
  void booleanAdditionalPropertiesAreSupported() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff = OpenApiCompare.fromLocations("issue-256_1.json", "issue-256_2.json");
    ConsoleRender render = new ConsoleRender();
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }
}
