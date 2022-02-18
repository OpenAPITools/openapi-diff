package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiChangedEndpoints;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class RequestDiffTest {
  private final String OPENAPI_DOC1 = "request_diff_1.yaml";
  private final String OPENAPI_DOC2 = "request_diff_2.yaml";

  @Test
  public void testDiffDifferent() {
    assertOpenApiChangedEndpoints(OPENAPI_DOC1, OPENAPI_DOC2);
  }

  @Test
  public void testSameTypeResponseSchemaDIff() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "response_schema_simple_object_type.yaml",
            "response_schema_simple_object_type_with_alloff.yaml");
    assertThat(changedOpenApi.isCompatible()).isTrue();
  }
}
