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
        OpenApiCompare.fromLocations(
            "issue-802-api-key-name-changed-1.yaml", "issue-802-api-key-name-changed-2.yaml");
    render.render(diff, outputStreamWriter);
    String output = outputStream.toString();
    assertThat(output).contains("##### `GET` /info");
    assertThat(output).contains("Security Requirements:");
    assertThat(output).contains("* Changed security requirement: `PAT`");
    // two-space list indent: a four-space indent would render as a code block
    assertThat(output).contains("  * Changed security scheme name: `Authorization` -> `pat`");
    assertThat(output).doesNotContain("\n    * Changed security scheme name");
    // changing the apiKey parameter name is wire-breaking unless configured otherwise
    assertThat(diff.isIncompatible()).isTrue();
    assertThat(output).contains("API changes broke backward compatibility");
  }

  @Test
  public void issue802_renderRemovedAndAddedApiKeySchemeInMarkdown() {
    MarkdownRender render = new MarkdownRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations(
            "issue-802-api-key-key-and-name-changed-1.yaml",
            "issue-802-api-key-key-and-name-changed-2.yaml");
    render.render(diff, outputStreamWriter);
    String output = outputStream.toString();
    assertThat(output)
        .contains("* Changed security requirement: `PAT2`")
        .contains("  * New security scheme: `PAT2: []`")
        .contains("  * Deleted security scheme: `PAT: []`");
    // no rename is guessed
    assertThat(output).doesNotContain("Changed security scheme name");
  }

  @Test
  public void issue802_renderAddedAlternativeSecurityRequirementInMarkdown() {
    MarkdownRender render = new MarkdownRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations(
            "issue-802-security-requirement-added-1.yaml",
            "issue-802-security-requirement-added-2.yaml");
    render.render(diff, outputStreamWriter);
    String output = outputStream.toString();
    assertThat(output).contains("* New security requirement: `petstore_auth: [read]`");
    assertThat(output).contains("API changes are backward compatible");
  }

  @Test
  public void issue802_apiKeyNameChangeIsCompatibleWhenConfigDisabled() {
    OpenApiDiffOptions options =
        OpenApiDiffOptions.builder()
            .configProperty(SECURITY_SCHEME_API_KEY_NAME_CHANGED.getPropertyName(), "false")
            .build();
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations(
            "issue-802-api-key-name-changed-1.yaml",
            "issue-802-api-key-name-changed-2.yaml",
            null,
            options);
    assertThat(diff.isDifferent()).isTrue();
    assertThat(diff.isCompatible()).isTrue();

    MarkdownRender render = new MarkdownRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    render.render(diff, outputStreamWriter);
    String output = outputStream.toString();
    assertThat(output).contains("Security Requirements:");
    assertThat(output).contains("* Changed security requirement: `PAT`");
    assertThat(output).contains("  * Changed security scheme name: `Authorization` -> `pat`");
    assertThat(output).contains("API changes are backward compatible");
  }
}
