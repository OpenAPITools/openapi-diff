package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class ContentDiffTest {

  private final String OPENAPI_DOC1 = "content_diff_1.yaml";
  private final String OPENAPI_DOC2 = "content_diff_2.yaml";

  @Test
  public void testContentDiffWithOneEmptyMediaType() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    assertThat(changedOpenApi.isIncompatible()).isTrue();
  }

  @Test
  public void testContentDiffWithEmptyMediaTypes() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC1);
    assertThat(changedOpenApi.isUnchanged()).isTrue();
  }

  @Test
  public void testSameContentDiff() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC2, OPENAPI_DOC2);
    assertThat(changedOpenApi.isUnchanged()).isTrue();
  }

  @Test
  public void testAddedResponseContentTypeDiff() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "content_type_response_add_1.yaml", "content_type_response_add_2.yaml");
    assertThat(changedOpenApi.isCompatible()).isTrue();
  }

  @Test
  public void testRemovedResponseContentTypeDiff() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "content_type_response_add_2.yaml", "content_type_response_add_1.yaml");
    assertThat(changedOpenApi.isCompatible()).isFalse();
  }

  @Test
  public void testAddedRequestContentTypeDiff() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "content_type_request_add_1.yaml", "content_type_request_add_2.yaml");
    assertThat(changedOpenApi.isCompatible()).isTrue();
  }

  @Test
  public void testRemovedRequestContentTypeDiff() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "content_type_request_add_2.yaml", "content_type_request_add_1.yaml");
    assertThat(changedOpenApi.isCompatible()).isFalse();
  }
}
