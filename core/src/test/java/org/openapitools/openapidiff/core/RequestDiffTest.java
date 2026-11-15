package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiChangedEndpoints;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;

public class RequestDiffTest {
  private final String OPENAPI_DOC1 = "request_diff_1.yaml";
  private final String OPENAPI_DOC2 = "request_diff_2.yaml";

  @Test
  public void testDiffDifferent() {
    assertOpenApiChangedEndpoints(OPENAPI_DOC1, OPENAPI_DOC2);
  }

  @Test
  public void testRequestBodyRequiredDecreasedIsCompatible() {
    // required -> optional: existing clients that keep sending the body still
    // work (issue #329)
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "bc_request_body_required_changed.yaml", "bc_request_body_base.yaml");
    assertThat(changedOpenApi.getChangedOperations()).hasSize(1);
    ChangedOperation operation = changedOpenApi.getChangedOperations().get(0);
    assertThat(operation.getRequestBody()).isNotNull();
    assertThat(operation.getRequestBody().isChangeRequired()).isTrue();
    assertThat(operation.getRequestBody().isChanged().isCompatible()).isTrue();
    assertThat(operation.isCompatible()).isTrue();
    assertThat(changedOpenApi.isChanged().isDifferent()).isTrue();
    assertThat(changedOpenApi.isChanged().isCompatible()).isTrue();
    assertThat(changedOpenApi.isChanged().isIncompatible()).isFalse();
  }

  @Test
  public void testRequestBodyRequiredIncreasedIsIncompatible() {
    // optional -> required: clients that do not send the body break
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "bc_request_body_base.yaml", "bc_request_body_required_changed.yaml");
    assertThat(changedOpenApi.getChangedOperations()).hasSize(1);
    ChangedOperation operation = changedOpenApi.getChangedOperations().get(0);
    assertThat(operation.getRequestBody()).isNotNull();
    assertThat(operation.getRequestBody().isChangeRequired()).isTrue();
    assertThat(operation.getRequestBody().isChanged().isIncompatible()).isTrue();
    assertThat(operation.isCompatible()).isFalse();
    assertThat(changedOpenApi.isChanged().isIncompatible()).isTrue();
  }
}
