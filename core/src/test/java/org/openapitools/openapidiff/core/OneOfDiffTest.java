package org.openapitools.openapidiff.core;

import static io.swagger.v3.oas.models.PathItem.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.ChangesResolver.getRequestBodyChangedSchema;
import static org.openapitools.openapidiff.core.ChangesResolver.getResponseBodyChangedSchema;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiChangedEndpoints;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.core.models.ParseOptions;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedSchema;
import org.openapitools.openapidiff.core.model.DiffResult;
import org.openapitools.openapidiff.core.model.schema.ChangedOneOfSchema;

public class OneOfDiffTest {

  private final String OPENAPI_DOC1 = "oneOf_diff_1.yaml";
  private final String OPENAPI_DOC2 = "oneOf_diff_2.yaml";
  private final String OPENAPI_DOC3 = "oneOf_diff_3.yaml";
  private final String OPENAPI_DOC4 = "composed_schema_1.yaml";
  private final String OPENAPI_DOC5 = "composed_schema_2.yaml";
  private final String OPENAPI_DOC6 = "oneOf_discriminator-changed_1.yaml";
  private final String OPENAPI_DOC7 = "oneOf_discriminator-changed_2.yaml";
  private final String OPENAPI_DOC8 = "oneOf_discriminator-missing_1.yaml";
  private final String OPENAPI_DOC9 = "oneOf_discriminator-missing_2.yaml";
  private final String OPENAPI_DOC10 = "unnamed_oneof_schema_1.yaml";
  private final String OPENAPI_DOC11 = "oneOf_to_anyOf_1.yaml";
  private final String OPENAPI_DOC12 = "oneOf_to_anyOf_2.yaml";
  private static final String MULTIPLE_ALIASES = "oneOf_discriminator-aliases_1.yaml";
  private static final String REORDERED_ALIASES = "oneOf_discriminator-aliases_2.yaml";

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  public void testOneOfDiscriminatorMappingOrder(boolean response) {
    OpenAPI oldSpec = readAliasSpec(MULTIPLE_ALIASES);
    OpenAPI newSpec = readAliasSpec(REORDERED_ALIASES);
    assertThat(compareAliases(oldSpec, newSpec, response).isChanged())
        .isEqualTo(DiffResult.NO_CHANGES);
    assertThat(OpenApiCompare.fromSpecifications(newSpec, oldSpec).isChanged())
        .isEqualTo(DiffResult.NO_CHANGES);
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  public void testOneOfDiscriminatorAliasAdded(boolean response) {
    OpenAPI oldSpec = readAliasSpec(MULTIPLE_ALIASES);
    OpenAPI newSpec = readAliasSpec(MULTIPLE_ALIASES);
    getAliasMapping(oldSpec).remove("z-type");

    ChangedOpenApi diff = compareAliases(oldSpec, newSpec, response);
    assertThat(diff.isChanged())
        .isEqualTo(response ? DiffResult.INCOMPATIBLE : DiffResult.COMPATIBLE);
    ChangedOneOfSchema oneOf = getOneOfChange(diff, response);
    assertThat(oneOf.getIncreased()).containsOnlyKeys("z-type");
    assertThat(oneOf.getMissing()).isEmpty();
    assertThat(oneOf.getChanged()).isEmpty();
    assertThat(oneOf.getNewMapping()).containsOnlyKeys("z-type", "a-type", "b-type");
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  public void testOneOfDiscriminatorAliasRemoved(boolean response) {
    OpenAPI oldSpec = readAliasSpec(MULTIPLE_ALIASES);
    OpenAPI newSpec = readAliasSpec(MULTIPLE_ALIASES);
    getAliasMapping(newSpec).remove("z-type");

    ChangedOpenApi diff = compareAliases(oldSpec, newSpec, response);
    assertThat(diff.isChanged())
        .isEqualTo(response ? DiffResult.COMPATIBLE : DiffResult.INCOMPATIBLE);
    ChangedOneOfSchema oneOf = getOneOfChange(diff, response);
    assertThat(oneOf.getMissing()).containsOnlyKeys("z-type");
    assertThat(oneOf.getIncreased()).isEmpty();
    assertThat(oneOf.getChanged()).isEmpty();
    assertThat(oneOf.getOldMapping()).containsOnlyKeys("z-type", "a-type", "b-type");
  }

  @ParameterizedTest
  @CsvSource({"false, false", "false, true", "true, false", "true, true"})
  public void testOneOfDiscriminatorAliasTargetChanged(boolean response, boolean reordered) {
    String location = reordered ? REORDERED_ALIASES : MULTIPLE_ALIASES;
    OpenAPI oldSpec = readAliasSpec(location);
    OpenAPI newSpec = readAliasSpec(location);
    getAliasMapping(newSpec).put("z-type", "#/components/schemas/B");

    ChangedOpenApi diff = compareAliases(oldSpec, newSpec, response);
    assertThat(diff.isChanged()).isEqualTo(DiffResult.INCOMPATIBLE);
    ChangedOneOfSchema oneOf = getOneOfChange(diff, response);
    assertThat(oneOf.getChanged()).containsOnlyKeys("z-type");
    assertThat(oneOf.getIncreased()).isEmpty();
    assertThat(oneOf.getMissing()).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  public void testOneOfSharedSchemaChangeReportedForEveryAlias(boolean response) {
    OpenAPI oldSpec = readAliasSpec(MULTIPLE_ALIASES);
    OpenAPI newSpec = readAliasSpec(MULTIPLE_ALIASES);
    newSpec.getComponents().getSchemas().get("A").addProperty("value", new IntegerSchema());

    ChangedOpenApi diff = compareAliases(oldSpec, newSpec, response);
    assertThat(diff.isChanged()).isEqualTo(DiffResult.INCOMPATIBLE);
    ChangedOneOfSchema oneOf = getOneOfChange(diff, response);
    assertThat(oneOf.getChanged()).containsOnlyKeys("z-type", "a-type");
    assertThat(oneOf.getIncreased()).isEmpty();
    assertThat(oneOf.getMissing()).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  public void testOneOfMultipleAliasesWithImplicitMapping(boolean response) {
    OpenAPI oldSpec = readAliasSpec(MULTIPLE_ALIASES);
    OpenAPI newSpec = readAliasSpec(REORDERED_ALIASES);
    getAliasMapping(oldSpec).remove("b-type");
    getAliasMapping(newSpec).remove("b-type");
    getAliasMapping(newSpec).put("B", "#/components/schemas/B");

    assertThat(compareAliases(oldSpec, newSpec, response).isChanged())
        .isEqualTo(DiffResult.NO_CHANGES);
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  public void testOneOfRecursiveSchemaWithMultipleAliases(boolean response) {
    OpenAPI oldSpec = readAliasSpec(MULTIPLE_ALIASES);
    OpenAPI newSpec = readAliasSpec(REORDERED_ALIASES);
    oldSpec
        .getComponents()
        .getSchemas()
        .get("A")
        .addProperty("child", new Schema<>().$ref("#/components/schemas/A"));
    newSpec
        .getComponents()
        .getSchemas()
        .get("A")
        .addProperty("child", new Schema<>().$ref("#/components/schemas/A"));
    newSpec.getComponents().getSchemas().get("A").addProperty("value", new IntegerSchema());

    ChangedOpenApi diff = compareAliases(oldSpec, newSpec, response);
    assertThat(diff.isChanged()).isEqualTo(DiffResult.INCOMPATIBLE);
    assertThat(getOneOfChange(diff, response).getChanged()).containsOnlyKeys("z-type", "a-type");
  }

  private OpenAPI readAliasSpec(String location) {
    return new OpenAPIParser().readLocation(location, null, new ParseOptions()).getOpenAPI();
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  public void testOneOfMutuallyRecursiveSchemasWithMultipleAliases(boolean response) {
    OpenAPI oldSpec = readAliasSpec(MULTIPLE_ALIASES);
    OpenAPI newSpec = readAliasSpec(REORDERED_ALIASES);
    addMutualReferences(oldSpec);
    addMutualReferences(newSpec);
    newSpec.getComponents().getSchemas().get("A").addProperty("value", new IntegerSchema());

    ChangedOpenApi diff = compareAliases(oldSpec, newSpec, response);
    assertThat(diff.isChanged()).isEqualTo(DiffResult.INCOMPATIBLE);
    assertThat(getOneOfChange(diff, response).getChanged()).containsKeys("z-type", "a-type");
  }

  private void addMutualReferences(OpenAPI spec) {
    Schema<?> schemaA = spec.getComponents().getSchemas().get("A");
    Schema<?> schemaB = spec.getComponents().getSchemas().get("B");
    schemaA.setNullable(true);
    schemaB.setNullable(true);
    schemaA.addProperty("peer", new Schema<>().$ref("#/components/schemas/B"));
    schemaB.addProperty("peer", new Schema<>().$ref("#/components/schemas/A"));
    schemaA.addRequiredItem("peer");
    schemaB.addRequiredItem("peer");
  }

  private Map<String, String> getAliasMapping(OpenAPI spec) {
    return spec.getPaths()
        .get("/state")
        .getPost()
        .getRequestBody()
        .getContent()
        .get("application/json")
        .getSchema()
        .getDiscriminator()
        .getMapping();
  }

  private ChangedOpenApi compareAliases(OpenAPI oldSpec, OpenAPI newSpec, boolean response) {
    if (response) {
      moveRequestBodyToResponse(oldSpec);
      moveRequestBodyToResponse(newSpec);
    }
    return OpenApiCompare.fromSpecifications(oldSpec, newSpec);
  }

  private void moveRequestBodyToResponse(OpenAPI spec) {
    Operation operation = spec.getPaths().get("/state").getPost();
    operation.getResponses().get("201").setContent(operation.getRequestBody().getContent());
    operation.setRequestBody(null);
  }

  private ChangedOneOfSchema getOneOfChange(ChangedOpenApi diff, boolean response) {
    ChangedSchema schema =
        response
            ? getResponseBodyChangedSchema(diff, POST, "/state", "201", "application/json")
            : getRequestBodyChangedSchema(diff, POST, "/state", "application/json");
    assertThat(schema).isNotNull();
    assertThat(schema.getOneOfSchema()).isNotNull();
    return schema.getOneOfSchema();
  }

  @Test
  public void testDiffSame() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
  }

  @Test
  public void testDiffDifferentMapping() {
    assertOpenApiChangedEndpoints(OPENAPI_DOC1, OPENAPI_DOC2);
  }

  @Test
  public void testDiffSameWithOneOf() {
    assertOpenApiAreEquals(OPENAPI_DOC2, OPENAPI_DOC3);
  }

  @Test
  public void testComposedSchema() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC4, OPENAPI_DOC5);
  }

  @Test
  public void testComposedSchemaDiff() {
    assertOpenApiAreEquals(OPENAPI_DOC10, OPENAPI_DOC10);
  }

  @Test
  public void testOneOfDiscrimitatorChanged() {
    // The oneOf 'discriminator' changed: 'realtype' -> 'othertype':
    assertOpenApiBackwardIncompatible(OPENAPI_DOC6, OPENAPI_DOC7);
  }

  @Test
  public void testOneOfDiscrimitatorMissingSameOrder() {
    assertOpenApiAreEquals(OPENAPI_DOC8, OPENAPI_DOC8);
  }

  @Test
  public void testOneOfDiscrimitatorMissingDifferentOrder() {
    assertOpenApiAreEquals(OPENAPI_DOC8, OPENAPI_DOC9);
  }

  @Test // issue #381
  public void testOneOfToAnyOfDoesNotThrow() {
    assertOpenApiChangedEndpoints(OPENAPI_DOC11, OPENAPI_DOC12);
  }
}
