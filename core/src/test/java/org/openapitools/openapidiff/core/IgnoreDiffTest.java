package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.compare.IgnoreDiff;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.slf4j.Logger;

public class IgnoreDiffTest {
  private final String OPENAPI_IGNOREDIFF1 = "ignore_diff_1.yaml";
  private final String OPENAPI_IGNOREDIFF2 = "ignore_diff_2.yaml";
  private final String OPENAPI_IGNOREDIFF3 = "ignore_diff_3.yaml";

  public static final Logger LOG = getLogger(TestUtils.class);

  @Test
  public void testEqualAfterIgnore() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_IGNOREDIFF1, OPENAPI_IGNOREDIFF2);

    assertThat(changedOpenApi.isIncompatible()).isTrue();

    ArrayList<String> ignoredAttributesList = new ArrayList<String>();
    ignoredAttributesList.add("x-internal");
    IgnoreDiff ignoreDiff = new IgnoreDiff(ignoredAttributesList, OPENAPI_IGNOREDIFF1);
    ignoreDiff.setSkippedPaths();
    changedOpenApi = ignoreDiff.removeSkippedPaths(changedOpenApi);

    LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
    assertThat(changedOpenApi.getNewEndpoints()).isEmpty();
    assertThat(changedOpenApi.getMissingEndpoints()).isEmpty();
    assertThat(changedOpenApi.getChangedOperations()).isEmpty();
    assertThat(changedOpenApi.isCompatible()).isTrue();
  }

  @Test
  public void testNotEqualWithoutIgnore() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_IGNOREDIFF2, OPENAPI_IGNOREDIFF3);
    
    assertThat(changedOpenApi.isIncompatible()).isTrue();

    ArrayList<String> ignoredAttributesList = new ArrayList<String>();
    ignoredAttributesList.add("x-internal");
    IgnoreDiff ignoreDiff = new IgnoreDiff(ignoredAttributesList, OPENAPI_IGNOREDIFF2);
    ignoreDiff.setSkippedPaths();
    changedOpenApi = ignoreDiff.removeSkippedPaths(changedOpenApi);

    LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
    assertThat(changedOpenApi.getNewEndpoints()).isEmpty();
    assertThat(changedOpenApi.getMissingEndpoints()).isEmpty();
    assertThat(changedOpenApi.getChangedOperations()).isNotEmpty();
    assertThat(changedOpenApi.isIncompatible()).isTrue();
  }

}
