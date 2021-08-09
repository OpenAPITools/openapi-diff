package org.openapitools.openapidiff.core;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.output.JsonRender;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonRenderTest {
  @Test
  public void renderDoesNotFailWhenPropertyHasBeenRemoved() {
    JsonRender render = new JsonRender();
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("missing_property_1.yaml", "missing_property_2.yaml");
    assertThat(render.render(diff)).isNotBlank();
  }
}
