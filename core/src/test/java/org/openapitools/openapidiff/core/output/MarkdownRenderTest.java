package org.openapitools.openapidiff.core.output;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SECURITY_REQUIREMENTS_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SECURITY_REQUIREMENT_SCHEMES_INCREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SECURITY_SCHEME_OAUTH2_AUTH_URL_CHANGED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SECURITY_SCHEME_OAUTH2_TOKEN_URL_CHANGED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SECURITY_SCHEME_SCOPES_INCREASED;

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
    // a blank line separates the heading from the nested scheme bullets
    assertThat(output).contains("Changed security requirement: `OAuth2`\n\n");
    assertThat(output).contains("Changed authorizationCode flow:");
    assertThat(output).contains("authorizationUrl: `` -> `some_value`");
    assertThat(output).contains("tokenUrl: `` -> `some_value`");
    // added and deleted security requirements are rendered with their scopes
    assertThat(output).contains("##### `GET` /orders");
    assertThat(output).contains("New security requirement: `OpenIdConnect: [openid, profile]`");
    assertThat(output).contains("Deleted security requirement: `BasicAuth: [user]`");
    // schemes added to or removed from a changed requirement are rendered with their scopes
    assertThat(output).contains("Changed security requirement: `OAuth2, NewApiKey`");
    assertThat(output).contains("New security scheme: `NewApiKey: [admin]`");
    assertThat(output).contains("Deleted security scheme: `OldApiKey: []`");
    assertThat(output).contains("Added scope value:");
    assertThat(output).contains("* `write`");
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
            .configProperty(SECURITY_REQUIREMENTS_DECREASED.getPropertyName(), "false")
            .configProperty(SECURITY_REQUIREMENT_SCHEMES_INCREASED.getPropertyName(), "false")
            .configProperty(SECURITY_SCHEME_SCOPES_INCREASED.getPropertyName(), "false")
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
    assertThat(output).contains("New security requirement: `OpenIdConnect: [openid, profile]`");
    assertThat(output).contains("Deleted security requirement: `BasicAuth: [user]`");
    assertThat(output).contains("New security scheme: `NewApiKey: [admin]`");
    assertThat(output).contains("Deleted security scheme: `OldApiKey: []`");
    assertThat(output).contains("API changes are backward compatible");
  }
}
