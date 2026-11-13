package org.openapitools.openapidiff.core.output;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SECURITY_SCHEME_API_KEY_NAME_CHANGED;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.OpenApiCompare;
import org.openapitools.openapidiff.core.compare.OpenApiDiffOptions;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class MarkdownRenderTest {
  @Test
  public void renderDoesNotFailWhenPropertyHasBeenRemoved() {
    MarkdownRender render = new MarkdownRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("missing_property_1.yaml", "missing_property_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void renderDoesNotCauseStackOverflowWithRecursiveDefinitions() {
    MarkdownRender render = new MarkdownRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff = OpenApiCompare.fromLocations("recursive_old.yaml", "recursive_new.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void renderDoesNotFailWhenHTTPStatusCodeIsRange() {
    MarkdownRender render = new MarkdownRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("range_statuscode_1.yaml", "range_statuscode_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void issue870_renderDoesNotFailWhenSchemaIsNullButExampleChanged() {
    MarkdownRender render = new MarkdownRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations(
            "issue-865-null-schema-1.yaml", "issue-865-null-schema-2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void issue802_renderChangedApiKeySchemeNameInMarkdown() {
    MarkdownRender render = new MarkdownRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("issue-802-api-key-1.yaml", "issue-802-api-key-2.yaml");
    render.render(diff, outputStreamWriter);
    String output = outputStream.toString();
    assertThat(output).contains("##### `GET` /info");
    assertThat(output).contains("Security Requirements:");
    assertThat(output).contains("New security requirement: `PAT: [context_management]`");
    assertThat(output).contains("Changed security scheme name: `Authorization` -> `pat`");
    // changing the apiKey parameter name is wire-breaking unless configured otherwise
    assertThat(diff.isIncompatible()).isTrue();
    assertThat(output).contains("API changes broke backward compatibility");
  }

  @Test
  public void issue802_apiKeyNameChangeIsCompatibleWhenConfigDisabled() {
    OpenApiDiffOptions options =
        OpenApiDiffOptions.builder()
            .configProperty(SECURITY_SCHEME_API_KEY_NAME_CHANGED.getPropertyName(), "false")
            .build();
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations(
            "issue-802-api-key-1.yaml", "issue-802-api-key-2.yaml", null, options);
    assertThat(diff.isDifferent()).isTrue();
    assertThat(diff.isCompatible()).isTrue();

    MarkdownRender render = new MarkdownRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    render.render(diff, outputStreamWriter);
    String output = outputStream.toString();
    assertThat(output).contains("Security Requirements:");
    assertThat(output).contains("New security requirement: `PAT: [context_management]`");
    assertThat(output).contains("Changed security scheme name: `Authorization` -> `pat`");
    assertThat(output).contains("API changes are backward compatible");
  }
}
