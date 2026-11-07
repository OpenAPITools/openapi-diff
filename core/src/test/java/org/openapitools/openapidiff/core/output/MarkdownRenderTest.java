package org.openapitools.openapidiff.core.output;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SECURITY_SCHEME_OAUTH2_AUTH_URL_CHANGED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SECURITY_SCHEME_OAUTH2_TOKEN_URL_CHANGED;

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
  public void issue893_renderChangedSecuritySchemeDetailsInMarkdown() {
    MarkdownRender render = new MarkdownRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("issue-893-oauth-1.yaml", "issue-893-oauth-2.yaml");
    render.render(diff, outputStreamWriter);
    String output = outputStream.toString();
    assertThat(output).contains("##### `POST` /users");
    assertThat(output).contains("Security Requirements:");
    assertThat(output).contains("Changed security requirement: `OAuth2`");
    assertThat(output).contains("Changed authorizationCode flow:");
    assertThat(output).contains("authorizationUrl: `` -> `some_value`");
    assertThat(output).contains("tokenUrl: `` -> `some_value`");
    // the compatibility status follows the diff model, which marks OAuth2 flow URL
    // changes as incompatible unless configured otherwise
    assertThat(diff.isIncompatible()).isTrue();
    assertThat(output).contains("API changes broke backward compatibility");
  }

  @Test
  public void issue893_securitySchemeUrlChangeIsCompatibleWhenConfigDisabled() {
    OpenApiDiffOptions options =
        OpenApiDiffOptions.builder()
            .configProperty(SECURITY_SCHEME_OAUTH2_AUTH_URL_CHANGED.getPropertyName(), "false")
            .configProperty(SECURITY_SCHEME_OAUTH2_TOKEN_URL_CHANGED.getPropertyName(), "false")
            .build();
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations(
            "issue-893-oauth-1.yaml", "issue-893-oauth-2.yaml", null, options);
    assertThat(diff.isDifferent()).isTrue();
    assertThat(diff.isCompatible()).isTrue();

    MarkdownRender render = new MarkdownRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    render.render(diff, outputStreamWriter);
    String output = outputStream.toString();
    assertThat(output).contains("Security Requirements:");
    assertThat(output).contains("Changed security requirement: `OAuth2`");
    assertThat(output).contains("authorizationUrl: `` -> `some_value`");
    assertThat(output).contains("API changes are backward compatible");
  }
}
