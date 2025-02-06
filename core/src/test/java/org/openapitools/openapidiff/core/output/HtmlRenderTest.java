package org.openapitools.openapidiff.core.output;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.OpenApiCompare;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class HtmlRenderTest {
  @Test
  public void renderDoesNotFailWhenPropertyHasBeenRemoved() {
    HtmlRender render = new HtmlRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("missing_property_1.yaml", "missing_property_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }
}
