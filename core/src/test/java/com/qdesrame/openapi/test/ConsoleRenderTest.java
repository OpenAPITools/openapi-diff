package com.qdesrame.openapi.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.qdesrame.openapi.diff.core.OpenApiCompare;
import com.qdesrame.openapi.diff.core.model.ChangedOpenApi;
import com.qdesrame.openapi.diff.core.output.ConsoleRender;
import org.junit.jupiter.api.Test;

public class ConsoleRenderTest {
  @Test
  public void renderDoesNotFailWhenPropertyHasBeenRemoved() {
    ConsoleRender render = new ConsoleRender();
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("missing_property_1.yaml", "missing_property_2.yaml");
    assertThat(render.render(diff)).isNotBlank();
  }
}
