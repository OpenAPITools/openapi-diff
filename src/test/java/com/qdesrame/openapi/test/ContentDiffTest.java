package com.qdesrame.openapi.test;

import com.qdesrame.openapi.diff.OpenApiCompare;
import com.qdesrame.openapi.diff.model.ChangedOpenApi;
import org.junit.Assert;
import org.junit.Test;

public class ContentDiffTest {

  private final String OPENAPI_DOC1 = "content_diff_1.yaml";
  private final String OPENAPI_DOC2 = "content_diff_2.yaml";

  @Test
  public void testContentDiffWithOneEmptyMediaType() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    Assert.assertTrue(changedOpenApi.isIncompatible());
  }

  @Test
  public void testContentDiffWithEmptyMediaTypes() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC1);
    Assert.assertTrue(changedOpenApi.isUnchanged());
  }

  @Test
  public void testSameContentDiff() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC2, OPENAPI_DOC2);
    Assert.assertTrue(changedOpenApi.isUnchanged());
  }
}
