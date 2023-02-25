package org.openapitools.openapidiff.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathParameterSchemaDiffTest {
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

  final String TEST_MSG_2 =
      "Testing: \n"
          + "1. Same path but different pathParameters\n"
          + "2. different parameters in the parameters: section\n"
          + "3. Parameters have same schema\n";

  @Test
  @DisplayName(
      "Same Path, different PathParams, Params in the `Parameters`: match pathParam, Different Schema")
  public void pathSamePathParamsDiffParamSameAsInPathButSchemaDiff() {
    final Logger logger = LoggerFactory.getLogger(PathParameterSchemaDiffTest.class);
    logger.info(TEST_MSG_1);
    String OPENAPI_DOC1 = "path_parameter_diff_param_schema_diff_old.yaml";
    String OPENAPI_DOC2 = "path_parameter_diff_param_schema_diff_new.yaml";
    ChangedOpenApi diff = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    Assertions.assertTrue(diff.isDifferent());
    Assertions.assertFalse(diff.isCompatible());
  }

  @Test
  @DisplayName(
      "Same Path, different PathParams, Params in the `Parameters`: match pathParam, same Schema")
  public void pathSamePathParamsDiffParamNameDiffSchemaSame() {
    final Logger logger = LoggerFactory.getLogger(PathParameterSchemaDiffTest.class);
    logger.info(TEST_MSG_2);
    String OPENAPI_DOC1 = "path_parameter_diff_param_name_diff_old.yaml";
    String OPENAPI_DOC2 = "path_parameter_diff_param_name_diff_new.yaml";
    ChangedOpenApi diff = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    Assertions.assertFalse(diff.isDifferent());
    Assertions.assertTrue(diff.isCompatible());
  }
}
