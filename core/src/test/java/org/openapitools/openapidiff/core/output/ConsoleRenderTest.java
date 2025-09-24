package org.openapitools.openapidiff.core.output;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.OpenApiCompare;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class ConsoleRenderTest {
  @Test
  public void renderDoesNotFailWhenPropertyHasBeenRemoved() {
    ConsoleRender render = new ConsoleRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("missing_property_1.yaml", "missing_property_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void renderDoesNotFailWhenHTTPStatusCodeIsRange() {
    ConsoleRender render = new ConsoleRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("range_statuscode_1.yaml", "range_statuscode_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void renderShowsWhatsDeletedSectionWhenEndpointIsDeleted() {
    ConsoleRender render = new ConsoleRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("delete_endpoint_1.yaml", "delete_endpoint_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).contains("What's Deleted");
  }

  @Test
  public void renderShowsWhatsNewSectionWhenEndpointIsAdded() {
    ConsoleRender render = new ConsoleRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("add_endpoint_1.yaml", "add_endpoint_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).contains("What's New");
  }

  @Test
  public void renderShowsWhatsDeprecatedSectionWhenEndpointIsDeprecated() {
    ConsoleRender render = new ConsoleRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("deprecate_endpoint_1.yaml", "deprecate_endpoint_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).contains("What's Deprecated");
  }

  @Test
  public void renderShowsWhatsChangedSectionWithCorrectFormattingWhenEndpointIsChanged() {
    ConsoleRender render = new ConsoleRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("change_endpoint_1.yaml", "change_endpoint_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString())
        .contains("What's Changed")
        .containsSubsequence("- GET    /widgets", "Parameter:", "- Changed query-param-1 in query");
  }
}
