package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedMediaType;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;
import org.openapitools.openapidiff.core.model.ChangedResponse;

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

  @Test
  public void issue805_identicalSpecsWithByteFormatExamplesHaveNoMediaTypeChanges() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "issue-805-byte-example-media-types-1.yaml",
            "issue-805-byte-example-media-types-1.yaml");
    assertThat(changedOpenApi.isUnchanged()).isTrue();
    assertThat(changedOpenApi.isDifferent()).isFalse();
    assertThat(changedOpenApi.isIncompatible()).isFalse();
    assertThat(changedOpenApi.isCompatible()).isTrue();
  }

  @Test
  public void issue805_byteFormatExampleChangeIsReportedAsBackwardCompatible() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "issue-805-byte-example-media-types-1.yaml",
            "issue-805-byte-example-media-types-2.yaml");
    assertThat(changedOpenApi.isDifferent()).isTrue();
    assertThat(changedOpenApi.isIncompatible()).isFalse();
    assertThat(changedOpenApi.isCompatible()).isTrue();

    List<ChangedOperation> changedOperations = changedOpenApi.getChangedOperations();
    assertThat(changedOperations).hasSize(1);
    ChangedResponse changedResponse =
        changedOperations.get(0).getApiResponses().getChanged().get("200");
    assertThat(changedResponse).isNotNull();

    // every change comes from the byte format example alone, so no schema change is attached
    Map<String, ChangedMediaType> changedMediaTypes = changedResponse.getContent().getChanged();
    assertThat(changedMediaTypes)
        .containsOnlyKeys(
            "text/csv",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/pdf",
            "application/x-bibtex",
            "application/marc",
            "text/xml");
    changedMediaTypes.values().forEach(mediaType -> assertThat(mediaType.getSchema()).isNull());
  }
}
