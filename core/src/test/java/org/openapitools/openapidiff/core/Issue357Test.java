package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class Issue357Test {

  private final String READONLY_REQUEST_OLD = "issue-357_1.json";
  private final String READONLY_REQUEST_NEW = "issue-357_2.json";
  private final String WRITEONLY_RESPONSE_OLD = "issue-357-writeonly-response_1.json";
  private final String WRITEONLY_RESPONSE_NEW = "issue-357-writeonly-response_2.json";
  private final String READONLY_RESPONSE_OLD = "issue-357-readonly-response_1.json";
  private final String READONLY_RESPONSE_NEW = "issue-357-readonly-response_2.json";

  @Test // issue #357
  public void changesInsideReadOnlyPropertyInRequestAreIgnored() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(READONLY_REQUEST_OLD, READONLY_REQUEST_NEW);

    assertThat(changedOpenApi.getChangedOperations()).isEmpty();
    assertThat(changedOpenApi.getChangedSchemas()).isEmpty();
    assertThat(changedOpenApi.isUnchanged()).isTrue();
  }

  @Test // counter-case: writeOnly in a response is still reported
  public void changesInsideWriteOnlyPropertyInResponseAreStillReported() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(WRITEONLY_RESPONSE_OLD, WRITEONLY_RESPONSE_NEW);

    assertThat(changedOpenApi.getChangedSchemas()).isNotEmpty();
    assertThat(changedOpenApi.isChanged().isDifferent()).isTrue();
    assertThat(changedOpenApi.isIncompatible()).isTrue();
  }

  @Test // response-side behavior must stay unchanged: readOnly in a response is still reported
  public void changesInsideReadOnlyPropertyInResponseAreStillReported() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(READONLY_RESPONSE_OLD, READONLY_RESPONSE_NEW);

    assertThat(changedOpenApi.getChangedOperations()).isNotEmpty();
    assertThat(changedOpenApi.isChanged().isDifferent()).isTrue();
    assertThat(changedOpenApi.isIncompatible()).isTrue();
  }
}
