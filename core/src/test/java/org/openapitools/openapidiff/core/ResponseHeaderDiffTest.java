package org.openapitools.openapidiff.core;

import static io.swagger.v3.oas.models.PathItem.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.ChangesResolver.getChangedResponseHeaders;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedHeader;
import org.openapitools.openapidiff.core.model.ChangedHeaders;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class ResponseHeaderDiffTest {

  private final String OPENAPI_DOC1 = "response_header_1.yaml";
  private final String OPENAPI_DOC2 = "response_header_2.yaml";

  @Test
  public void testResponseHeadersDescriptionChanges() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedHeaders changedResponseHeaders =
        getChangedResponseHeaders(changedOpenApi, GET, "/response/headers/description", "200");

    assertThat(changedResponseHeaders).isNotNull();
    Map<String, ChangedHeader> changedHeaders = changedResponseHeaders.getChanged();

    assertThat(changedHeaders).containsKey("x-header-description-changed");
    assertThat(changedHeaders.get("x-header-description-changed").getDescription()).isNotNull();
    assertThat(changedHeaders.get("x-header-description-changed").getDescription().getLeft())
        .isEqualTo("old description");
    assertThat(changedHeaders.get("x-header-description-changed").getDescription().getRight())
        .isEqualTo("new description");

    assertThat(changedHeaders).containsKey("x-header-description-added");
    assertThat(changedHeaders.get("x-header-description-added").getDescription()).isNotNull();
    assertThat(changedHeaders.get("x-header-description-added").getDescription().getLeft())
        .isNull();
    assertThat(changedHeaders.get("x-header-description-added").getDescription().getRight())
        .isEqualTo("added description");

    assertThat(changedHeaders).containsKey("x-header-description-removed");
    assertThat(changedHeaders.get("x-header-description-removed").getDescription()).isNotNull();
    assertThat(changedHeaders.get("x-header-description-removed").getDescription().getLeft())
        .isEqualTo("old description");
    assertThat(changedHeaders.get("x-header-description-removed").getDescription().getRight())
        .isNull();
  }

  @Test
  public void testResponseHeadersRequiredChanges() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedHeaders changedResponseHeaders =
        getChangedResponseHeaders(changedOpenApi, GET, "/response/headers/required", "200");

    assertThat(changedResponseHeaders).isNotNull();
    Map<String, ChangedHeader> changedHeaders = changedResponseHeaders.getChanged();

    assertThat(changedHeaders).containsKey("x-header-required-changed");
  }

  // TODO
  // @Test
  public void testResponseHeadersSchemaChanges() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedHeaders changedResponseHeaders =
        getChangedResponseHeaders(changedOpenApi, GET, "/response/headers/schema", "200");

    assertThat(changedResponseHeaders).isNotNull();
    Map<String, ChangedHeader> changedHeaders = changedResponseHeaders.getChanged();

    // TODO response schema type changes are not handled
    assertThat(changedHeaders).containsKey("x-header-schema-changed");
  }

  // TODO
  // @Test // issue #485
  public void testResponseHeadersDeprecatedChanges() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedHeaders changedResponseHeaders =
        getChangedResponseHeaders(changedOpenApi, GET, "/response/headers/deprecated", "200");

    assertThat(changedResponseHeaders).isNotNull();
    Map<String, ChangedHeader> changedHeaders = changedResponseHeaders.getChanged();
    assertThat(changedHeaders).containsKey("x-header-becomes-deprecated");
    assertThat(changedHeaders.get("x-header-becomes-deprecated").isDeprecated()).isTrue();
    assertThat(changedHeaders).containsKey("x-header-becomes-not-deprecated");
    assertThat(changedHeaders.get("x-header-becomes-not-deprecated").isDeprecated()).isFalse();
  }
}
