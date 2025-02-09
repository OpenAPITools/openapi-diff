package org.openapitools.openapidiff.core;

import static io.swagger.v3.oas.models.PathItem.HttpMethod.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.ChangesResolver.getChangedOperation;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;
import org.openapitools.openapidiff.core.model.DiffResult;

public class OperationDiffTest {

  private final String OPENAPI_DOC1 = "operation_diff_1.yaml";
  private final String OPENAPI_DOC2 = "operation_diff_2.yaml";

  @Test
  public void testOperationIdChanged() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedOperation changedOperation = getChangedOperation(changedOpenApi, GET, "/operation/operation-id");

    assertThat(changedOperation).isNotNull();
    assertThat(changedOperation.isChanged()).isEqualTo(DiffResult.METADATA);
    assertThat(changedOperation.getOperationId().getRight()).isEqualTo("changed");
  }

  @Test
  public void testOperationSummaryChanged() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedOperation changedOperation = getChangedOperation(changedOpenApi, GET, "/operation/summary");

    assertThat(changedOperation).isNotNull();
    assertThat(changedOperation.isChanged()).isEqualTo(DiffResult.METADATA);
    assertThat(changedOperation.getSummary().getRight()).isEqualTo("changed");
  }

  @Test
  public void testOperationDescriptionChanged() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedOperation changedOperation = getChangedOperation(changedOpenApi, GET, "/operation/description");

    assertThat(changedOperation).isNotNull();
    assertThat(changedOperation.isChanged()).isEqualTo(DiffResult.METADATA);
    assertThat(changedOperation.getDescription().getRight()).isEqualTo("changed");
  }

  @Test
  public void testOperationBecomesDeprecated() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedOperation changedOperation = getChangedOperation(changedOpenApi, GET, "/operation/becomes-deprecated");

    assertThat(changedOperation).isNotNull();
    assertThat(changedOperation.isDeprecated()).isTrue();
  }

  @Test
  public void testOperationBecomesNotDeprecated() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedOperation changedOperation = getChangedOperation(changedOpenApi, GET, "/operation/becomes-not-deprecated");

    assertThat(changedOperation).isNotNull();
    assertThat(changedOperation.isDeprecated()).isTrue();
  }
}
