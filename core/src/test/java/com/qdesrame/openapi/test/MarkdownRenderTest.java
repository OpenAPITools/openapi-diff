package com.qdesrame.openapi.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.qdesrame.openapi.diff.core.OpenApiCompare;
import com.qdesrame.openapi.diff.core.model.ChangedOpenApi;
import com.qdesrame.openapi.diff.core.output.MarkdownRender;
import org.junit.jupiter.api.Test;

public class MarkdownRenderTest {
  @Test
  public void renderDoesNotFailWhenPropertyHasBeenRemoved() {
    MarkdownRender render = new MarkdownRender();
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("missing_property_1.yaml", "missing_property_2.yaml");
    assertThat(render.render(diff)).isNotBlank();
  }
}
