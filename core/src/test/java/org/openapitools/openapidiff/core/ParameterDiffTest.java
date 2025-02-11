package org.openapitools.openapidiff.core;

import static io.swagger.v3.oas.models.PathItem.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.openapitools.openapidiff.core.ChangesResolver.getChangedOperation;
import static org.openapitools.openapidiff.core.ChangesResolver.getChangedParameter;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiChangedEndpoints;

import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;
import org.openapitools.openapidiff.core.model.ChangedParameter;
import org.openapitools.openapidiff.core.model.ChangedParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParameterDiffTest {

  private final String OPENAPI_DOC1 = "parameterDiff/parameter_diff_1.yaml";
  private final String OPENAPI_DOC2 = "parameterDiff/parameter_diff_2.yaml";
  private final String OVERLOADED_PARAMETERS = "parameterDiff/parameters_overloading.yaml";
  private final String DUPLICATED_PARAMETER_TYPES = "parameterDiff/parameters_overloading_2.yaml";

  @Test
  public void testAddParameter() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedOperation changedOperation =
        getChangedOperation(changedOpenApi, GET, "/parameter/added");

    assertThat(changedOperation).isNotNull();
    List<Parameter> increasedParams =
        Optional.ofNullable(changedOperation.getParameters())
            .map(ChangedParameters::getIncreased)
            .orElse(Collections.emptyList());
    assertThat(increasedParams.size()).isEqualTo(1);
    assertThat(increasedParams.get(0).getName()).isEqualTo("param");
  }

  @Test
  public void testRemoveParameter() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedOperation changedOperation =
        getChangedOperation(changedOpenApi, GET, "/parameter/removed");

    assertThat(changedOperation).isNotNull();
    List<Parameter> missingParams =
        Optional.ofNullable(changedOperation.getParameters())
            .map(ChangedParameters::getMissing)
            .orElse(Collections.emptyList());
    assertThat(missingParams.size()).isEqualTo(1);
    assertThat(missingParams.get(0).getName()).isEqualTo("param");
  }

  @Test
  public void testParameterBecomesDeprecated() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedParameter changedParameter =
        getChangedParameter(changedOpenApi, GET, "/parameter/becomes-deprecated", "X-Header");

    assertThat(changedParameter).isNotNull();
    assertThat(changedParameter.isDeprecated()).isTrue();
  }

  @Test
  public void testParameterBecomesNotDeprecated() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedParameter changedParameter =
        getChangedParameter(changedOpenApi, GET, "/parameter/becomes-not-deprecated", "X-Header");

    assertThat(changedParameter).isNotNull();
    assertThat(changedParameter.isDeprecated()).isTrue();
  }

  @Test
  public void testParameterRequiredChanged() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedParameter changedParameter =
        getChangedParameter(changedOpenApi, GET, "/parameter/required", "param");

    assertThat(changedParameter).isNotNull();
    assertThat(changedParameter.isChangeRequired()).isTrue();
  }

  @Test
  public void testParameterDescriptionChanged() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedParameter changedParameter =
        getChangedParameter(changedOpenApi, GET, "/parameter/description", "param");

    assertThat(changedParameter).isNotNull();
    assertThat(changedParameter.getDescription().getRight()).isEqualTo("changed");
  }

  @Test
  public void testParameterExplodeChanged() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedParameter changedParameter =
        getChangedParameter(changedOpenApi, GET, "/parameter/explode", "param");

    assertThat(changedParameter).isNotNull();
    assertThat(changedParameter.isChangeExplode()).isTrue();
  }

  @Test
  public void testParameterStyleChanged() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    ChangedParameter changedParameter =
        getChangedParameter(changedOpenApi, GET, "/parameter/style", "param");

    assertThat(changedParameter).isNotNull();
    assertThat(changedParameter.isChangeStyle()).isTrue();
  }

  @Test
  void testDiffWithOverloadedParameterTypes() {
    assertDoesNotThrow(
        () -> OpenApiCompare.fromLocations(OVERLOADED_PARAMETERS, OVERLOADED_PARAMETERS));
    assertOpenApiAreEquals(OVERLOADED_PARAMETERS, OVERLOADED_PARAMETERS);
  }

  @Test
  void testDiffWithDuplicatedParameterTypes() {
    assertThrows(
        IllegalArgumentException.class,
        () -> OpenApiCompare.fromLocations(DUPLICATED_PARAMETER_TYPES, DUPLICATED_PARAMETER_TYPES),
        "Two path items have the same signature: /projects/{}");
  }

  @Test
  public void issue458MaximumDecreased() {
    assertOpenApiChangedEndpoints(
        "parameterDiff/issue-458-integer-limits_1.yaml",
        "parameterDiff/issue-458-integer-limits_2.yaml");
  }

  @Test
  public void issue458MaximumIncreased() {
    assertOpenApiChangedEndpoints(
        "parameterDiff/issue-458-integer-limits_1.yaml",
        "parameterDiff/issue-458-integer-limits_3.yaml");
  }

  @Test
  public void issue458MinimumDecreased() {
    assertOpenApiChangedEndpoints(
        "parameterDiff/issue-458-integer-limits_1.yaml",
        "parameterDiff/issue-458-integer-limits_4.yaml");
  }

  @Test
  public void issue458MinimumIncreased() {
    assertOpenApiChangedEndpoints(
        "parameterDiff/issue-458-integer-limits_1.yaml",
        "parameterDiff/issue-458-integer-limits_5.yaml");
  }

  @Test
  public void issue458IntegerFormatChanged() {
    assertOpenApiChangedEndpoints(
        "parameterDiff/issue-458-integer-limits_1.yaml",
        "parameterDiff/issue-458-integer-limits_6.yaml");
  }

  @Test
  public void issue458ExclusiveMinimumChanged() {
    assertOpenApiChangedEndpoints(
        "parameterDiff/issue-458-integer-limits_1.yaml",
        "parameterDiff/issue-458-integer-limits_7.yaml");
  }

  @Test
  public void issue458ExclusiveMaximumChanged() {
    assertOpenApiChangedEndpoints(
        "parameterDiff/issue-458-integer-limits_1.yaml",
        "parameterDiff/issue-458-integer-limits_8.yaml");
  }

  @Test
  public void issue458ExclusiveMinimumRemoved() {
    assertOpenApiChangedEndpoints(
        "parameterDiff/issue-458-integer-limits_1.yaml",
        "parameterDiff/issue-458-integer-limits_9.yaml");
  }

  @Test
  public void issue458ExclusiveMaximumRemoved() {
    assertOpenApiChangedEndpoints(
        "parameterDiff/issue-458-integer-limits_1.yaml",
        "parameterDiff/issue-458-integer-limits_10.yaml");
  }

  @Test
  public void issue458ExclusiveMaximumTrueToFalse() {
    assertOpenApiChangedEndpoints(
        "parameterDiff/issue-458-integer-limits_11.yaml",
        "parameterDiff/issue-458-integer-limits_12.yaml");
  }

  @Test
  public void issue458ExclusiveMinimumTrueToFalse() {
    assertOpenApiChangedEndpoints(
        "parameterDiff/issue-458-integer-limits_11.yaml",
        "parameterDiff/issue-458-integer-limits_13.yaml");
  }

  @Test
  public void issue458ExclusiveMaximumTrueRemoved() {
    assertOpenApiChangedEndpoints(
        "parameterDiff/issue-458-integer-limits_11.yaml",
        "parameterDiff/issue-458-integer-limits_12.yaml");
  }

  @Test
  public void issue458ExclusiveMinimumTrueRemoved() {
    assertOpenApiChangedEndpoints(
        "parameterDiff/issue-458-integer-limits_11.yaml",
        "parameterDiff/issue-458-integer-limits_13.yaml");
  }

  @Test
  public void issue488RenameParameterAddAndRemoveParameterReturnFalse() {
    assertOpenApiChangedEndpoints(
        "parameterDiff/issue-488-1.json", "parameterDiff/issue-488-2.json");
  }

  final String TEST_MSG_1 =
      "Testing: \n"
          + "1. Same path but different pathParameters\n"
          + "2. different parameters in the parameters: section\n"
          + "3. Parameters have different schema\n"
          + "eg:\n"
          + "old path   -- students/{id}\n"
          + "old schema -- id: integer\n"
          + "new path   -- students/{username}\n"
          + "new schema -- username: string";

  @Test
  @DisplayName(
      "Same Path, different PathParams, Params in the `Parameters`: match pathParam, Different Schema")
  public void pathSamePathParamsDiffParamSameAsInPathButSchemaDiff() {
    final Logger logger = LoggerFactory.getLogger(ParameterDiffTest.class);
    logger.info(TEST_MSG_1);
    String OPENAPI_DOC1 = "parameterDiff/path_parameter_diff_param_schema_diff_old.yaml";
    String OPENAPI_DOC2 = "parameterDiff/path_parameter_diff_param_schema_diff_new.yaml";
    ChangedOpenApi diff = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    Assertions.assertTrue(diff.isDifferent());
    Assertions.assertFalse(diff.isCompatible());
  }

  final String TEST_MSG_2 =
      "Testing: \n"
          + "1. Same path but different pathParameters\n"
          + "2. different parameters in the parameters: section\n"
          + "3. Parameters have same schema\n";

  @Test
  @DisplayName(
      "Same Path, different PathParams, Params in the `Parameters`: match pathParam, same Schema")
  public void pathSamePathParamsDiffParamNameDiffSchemaSame() {
    final Logger logger = LoggerFactory.getLogger(ParameterDiffTest.class);
    logger.info(TEST_MSG_2);
    String OPENAPI_DOC1 = "parameterDiff/path_parameter_diff_param_name_diff_old.yaml";
    String OPENAPI_DOC2 = "parameterDiff/path_parameter_diff_param_name_diff_new.yaml";
    ChangedOpenApi diff = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    Assertions.assertFalse(diff.isDifferent());
    Assertions.assertTrue(diff.isCompatible());
  }
}
