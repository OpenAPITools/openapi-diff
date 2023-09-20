package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.output.AsciidocRender;

public class AsciidocRenderTest {
  @Test
  public void renderDoesNotFailWhenPropertyHasBeenRemoved() {
    AsciidocRender render = new AsciidocRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("missing_property_1.yaml", "missing_property_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void renderDoesNotCauseStackOverflowWithRecursiveDefinitions() {
    AsciidocRender render = new AsciidocRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff = OpenApiCompare.fromLocations("recursive_old.yaml", "recursive_new.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void renderDoesNotFailWhenHTTPStatusCodeIsRange() {
    AsciidocRender render = new AsciidocRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("range_statuscode_1.yaml", "range_statuscode_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void validateAsciiDocChangeFile() {
    AsciidocRender render = new AsciidocRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("missing_property_1.yaml", "missing_property_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString())
        .isEqualTo(
            "= TITLE (v 1.0.0)\n"
                + ":reproducible:\n"
                + ":sectlinks:\n"
                + ":toc:\n"
                + "\n"
                + "== What's Changed\n"
                + "=== GET   /\n"
                + "* Return Type:\n"
                + "** Changed default \n"
                + "** Media types:\n"
                + "*** Changed application/json\n"
                + "*** Schema:\n"
                + "Backward compatible\n"
                + "\n"
                + "\n"
                + "NOTE: API changes are backward compatible\n");
  }

  @Test
  public void validateAsciiDocRangeStatus() {
    AsciidocRender render = new AsciidocRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("range_statuscode_1.yaml", "range_statuscode_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString())
        .isEqualTo(
            "= PROJECTS API (v 1.0.0)\n"
                + ":reproducible:\n"
                + ":sectlinks:\n"
                + ":toc:\n"
                + "\n"
                + "== What's Changed\n"
                + "=== GET   /pet/\n"
                + "* Return Type:\n"
                + "** Add 4XX \n"
                + "** Deleted 405 Method Not Allowed\n"
                + "\n"
                + "\n"
                + "WARNING: API changes broke backward compatibility\n");
  }
}
