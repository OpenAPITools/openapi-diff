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

  @Test
  public void issue865_renderDoesNotFailWhenSchemaIsNullButExampleChanged() {
    HtmlRender render = new HtmlRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations(
            "issue-865-null-schema-1.yaml", "issue-865-null-schema-2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void issue865_renderWithShowAllChangesDoesNotFailWhenSchemaIsNullButExampleChanged() {
    HtmlRender render = new HtmlRender(true);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations(
            "issue-865-null-schema-1.yaml", "issue-865-null-schema-2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void issue857_renderShowsChangedOperationDescription() {
    HtmlRender render = new HtmlRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations(
            "issue-857-operation-description-1.json", "issue-857-operation-description-2.json");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).contains("Changed description:");
    assertThat(outputStream.toString()).contains("<del class=\"comment\">Sample description</del>");
    assertThat(outputStream.toString())
        .contains("<span class=\"comment\">Sample description changed</span>");
  }

  @Test
  public void issue857_renderWithShowAllChangesShowsChangedOperationDescription() {
    HtmlRender render = new HtmlRender(true);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations(
            "issue-857-operation-description-1.json", "issue-857-operation-description-2.json");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).contains("Changed description:");
    assertThat(outputStream.toString()).contains("Sample description changed");
  }
}
