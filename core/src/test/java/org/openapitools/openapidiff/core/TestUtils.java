package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import javax.annotation.Nullable;

import org.openapitools.openapidiff.core.compare.OpenApiDiffOptions;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;
import org.openapitools.openapidiff.core.model.DiffResult;
import org.slf4j.Logger;

public class TestUtils {
  public static final Logger LOG = getLogger(TestUtils.class);

  public static void assertOpenApiAreEquals(String oldSpec, String newSpec) {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
    LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
    assertThat(changedOpenApi.getNewEndpoints()).isEmpty();
    assertThat(changedOpenApi.getMissingEndpoints()).isEmpty();
    assertThat(changedOpenApi.getChangedOperations()).isEmpty();
  }

  public static void assertOpenApiChangedEndpoints(String oldSpec, String newSpec) {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
    LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
    assertThat(changedOpenApi.getNewEndpoints()).isEmpty();
    assertThat(changedOpenApi.getMissingEndpoints()).isEmpty();
    assertThat(changedOpenApi.getChangedOperations()).isNotEmpty();
  }

  public static void assertSpecUnchanged(String oldSpec, String newSpec) {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
    LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
    assertThat(changedOpenApi.isUnchanged()).isTrue();
  }

  public static void assertSpecChangedButCompatible(String oldSpec, String newSpec) {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
    DiffResult diffResult = changedOpenApi.isChanged();
    LOG.info("Result: {}", diffResult.getValue());
    assertThat(diffResult.isDifferent()).isTrue();
    assertThat(diffResult.isCompatible()).isTrue();
  }

  public static void assertSpecIncompatible(
      String oldSpec, String newSpec, BackwardIncompatibleProp prop) {
    OpenApiDiffOptions.Builder builder = OpenApiDiffOptions.builder();
    // Expect incompatible when BackwardIncompatibleProp enabled
    builder.configProperty(prop.getPropertyName(), "true");
    OpenApiDiffOptions optsIncompat = builder.build();
    ChangedOpenApi apiIncompat = OpenApiCompare.fromLocations(oldSpec, newSpec, null, optsIncompat);
    LOG.info("Result: {}", apiIncompat.isChanged().getValue());
    assertThat(apiIncompat.isIncompatible()).isTrue();
    // Expect changed but compatible when BackwardIncompatibleProp disabled
    builder.configProperty(prop.getPropertyName(), "false");
    OpenApiDiffOptions optsCompat = builder.build();
    ChangedOpenApi apiCompat = OpenApiCompare.fromLocations(oldSpec, newSpec, null, optsCompat);
    LOG.info("Result: {}", apiCompat.isChanged().getValue());
    assertThat(apiCompat.isDifferent()).isTrue();
    assertThat(apiCompat.isCompatible()).isTrue();
  }

  public static void assertOpenApiBackwardCompatible(
      String oldSpec, String newSpec, boolean isDiff) {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
    LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
    assertThat(changedOpenApi.isCompatible()).isTrue();
  }

  public static void assertOpenApiBackwardIncompatible(String oldSpec, String newSpec) {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
    LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
    assertThat(changedOpenApi.isIncompatible()).isTrue();
  }

  public static List<ChangedOperation> getOpenApiChangedOperations(String oldSpec, String newSpec) {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
    return changedOpenApi.getChangedOperations();
  }
}
