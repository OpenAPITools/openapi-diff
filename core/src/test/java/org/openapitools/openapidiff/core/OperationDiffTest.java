package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.DiffResult;

public class OperationDiffTest {

  private final String OPENAPI_DOC1 = "operation_diff_1.yaml";
  private final String OPENAPI_DOC2 = "operation_diff_2.yaml";

  @Test
  public void testContentDiffWithOneEmptyMediaType() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    assertThat(changedOpenApi.isChanged()).isEqualTo(DiffResult.METADATA);
    assertThat(changedOpenApi.isDifferent()).isTrue();
    assertThat(changedOpenApi.getChangedOperations().size()).isEqualTo(1);
    assertThat(changedOpenApi.getChangedOperations().get(0).getOperationId().isDifferent())
        .isTrue();
  }
}
