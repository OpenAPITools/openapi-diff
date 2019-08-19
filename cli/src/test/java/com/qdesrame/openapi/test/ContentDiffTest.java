package com.qdesrame.openapi.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.qdesrame.openapi.diff.OpenApiCompare;
import com.qdesrame.openapi.diff.model.ChangedOpenApi;
import org.junit.jupiter.api.Test;

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
}
