package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class PathDiffTest {

  private final String OPENAPI_PATH1 = "path_1.yaml";
  private final String OPENAPI_PATH2 = "path_2.yaml";
  private final String OPENAPI_PATH3 = "path_3.yaml";
  private final String OPENAPI_PATH4 = "path_4.yaml";
  private final String OPENAPI_PATH5 = "path_5.yaml";
  private final String OPENAPI_PATH6 = "path_6.yaml";

  @Test
  public void testEqual() {
    assertOpenApiAreEquals(OPENAPI_PATH1, OPENAPI_PATH2);
  }

  @Test
  public void testMultiplePathWithSameSignature() {
    assertThrows(
        IllegalArgumentException.class, () -> assertOpenApiAreEquals(OPENAPI_PATH3, OPENAPI_PATH3));
  }

  @Test
  public void testSameTemplateDifferentMethods() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_PATH1, OPENAPI_PATH4);
    assertThat(changedOpenApi.getNewEndpoints())
        .hasSize(1)
        .satisfiesExactly(
            endpoint ->
                assertThat(endpoint.getOperation().getOperationId()).isEqualTo("deletePet"));
    assertThat(changedOpenApi.isCompatible()).isTrue();
  }

  @Test
  public void testDiffWithSimilarBeginningPaths() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_PATH5, OPENAPI_PATH6);
    ChangedOpenApi diff = OpenApiCompare.fromSpecifications(
            changedOpenApi.getOldSpecOpenApi(), changedOpenApi.getNewSpecOpenApi());
    assertThat(diff.getChangedOperations()).isEmpty();
  }
}
