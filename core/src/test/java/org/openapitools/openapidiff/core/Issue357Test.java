package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedSchema;

public class Issue357Test {

  private final String READONLY_REQUEST_OLD = "issue-357_1.json";
  private final String READONLY_REQUEST_NEW = "issue-357_2.json";
  private final String WRITEONLY_RESPONSE_OLD = "issue-357-writeonly-response_1.json";
  private final String WRITEONLY_RESPONSE_NEW = "issue-357-writeonly-response_2.json";
  private final String READONLY_RESPONSE_OLD = "issue-357-readonly-response_1.json";
  private final String READONLY_RESPONSE_NEW = "issue-357-readonly-response_2.json";
  private final String READONLY_REQUIRED_OLD = "issue-357-readonly-required_1.json";
  private final String READONLY_REQUIRED_NEW = "issue-357-readonly-required_2.json";
  private final String READONLY_REF_OLD = "issue-357-readonly-ref_1.json";
  private final String READONLY_REF_NEW = "issue-357-readonly-ref_2.json";
  private final String READONLY_TRANSITION_OFF_OLD = "issue-357-readonly-transition-off_1.json";
  private final String READONLY_TRANSITION_OFF_NEW = "issue-357-readonly-transition-off_2.json";
  private final String READONLY_TRANSITION_ON_OLD = "issue-357-readonly-transition-on_1.json";
  private final String READONLY_TRANSITION_ON_NEW = "issue-357-readonly-transition-on_2.json";

  @Test // issue #357
  public void changesInsideReadOnlyPropertyInRequestAreIgnored() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(READONLY_REQUEST_OLD, READONLY_REQUEST_NEW);

    assertThat(changedOpenApi.getChangedOperations()).isEmpty();
    assertThat(changedOpenApi.getChangedSchemas()).isEmpty();
    assertThat(changedOpenApi.isUnchanged()).isTrue();
  }

  @Test // changes to the parent `required` list of a skipped readOnly property are ignored too
  public void requiredChangeForReadOnlyPropertyInRequestIsIgnored() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(READONLY_REQUIRED_OLD, READONLY_REQUIRED_NEW);

    assertThat(changedOpenApi.getChangedOperations()).isEmpty();
    assertThat(changedOpenApi.getChangedSchemas()).isEmpty();
    assertThat(changedOpenApi.isUnchanged()).isTrue();
  }

  @Test // a property referenced via `$ref` that resolves to a readOnly schema is ignored too,
  // including parent `required` changes for the referenced property
  public void changesInsideReferencedReadOnlyPropertyInRequestAreIgnored() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(READONLY_REF_OLD, READONLY_REF_NEW);

    assertThat(changedOpenApi.getChangedOperations()).isEmpty();
    assertThat(changedOpenApi.getChangedSchemas()).isEmpty();
    assertThat(changedOpenApi.isUnchanged()).isTrue();
  }

  @Test // readOnly true -> false: the flag change is a real wire change and must be reported
  public void readOnlyFlagRemovedInRequestStillReportsNestedChanges() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(READONLY_TRANSITION_OFF_OLD, READONLY_TRANSITION_OFF_NEW);

    assertThat(changedOpenApi.getChangedSchemas()).isNotEmpty();
    assertThat(changedOpenApi.isChanged().isDifferent()).isTrue();
    assertThat(changedOpenApi.isIncompatible()).isTrue();
    assertThat(anyNestedTypeChange(changedOpenApi)).isTrue();
  }

  @Test // readOnly false -> true: the flag change is a real wire change and must be reported
  public void readOnlyFlagAddedInRequestStillReportsNestedChanges() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(READONLY_TRANSITION_ON_OLD, READONLY_TRANSITION_ON_NEW);

    assertThat(changedOpenApi.getChangedSchemas()).isNotEmpty();
    assertThat(changedOpenApi.isChanged().isDifferent()).isTrue();
    assertThat(changedOpenApi.isIncompatible()).isTrue();
    assertThat(anyNestedTypeChange(changedOpenApi)).isTrue();
  }

  private static boolean anyNestedTypeChange(ChangedOpenApi changedOpenApi) {
    return changedOpenApi.getChangedSchemas().stream()
        .flatMap(changedSchema -> changedSchema.getChangedProperties().values().stream())
        .anyMatch(ChangedSchema::isChangedType);
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
