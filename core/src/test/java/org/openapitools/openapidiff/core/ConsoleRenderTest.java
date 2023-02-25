package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.output.ConsoleRender;

public class ConsoleRenderTest {
  @Test
  public void renderDoesNotFailWhenPropertyHasBeenRemoved() {
    ConsoleRender render = new ConsoleRender();
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("missing_property_1.yaml", "missing_property_2.yaml");
    assertThat(render.render(diff)).isNotBlank();
  }

  @Test
  public void renderDoesNotFailWhenHTTPStatusCodeIsRange() {
    ConsoleRender render = new ConsoleRender();
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("range_statuscode_1.yaml", "range_statuscode_2.yaml");
    assertThat(render.render(diff)).isNotBlank();
  }
}
