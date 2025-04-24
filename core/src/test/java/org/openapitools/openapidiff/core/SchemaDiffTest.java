package org.openapitools.openapidiff.core;

import static io.swagger.v3.oas.models.PathItem.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openapitools.openapidiff.core.ChangesResolver.getRequestBodyChangedSchema;
import static org.openapitools.openapidiff.core.ChangesResolver.getResponseBodyChangedSchema;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardCompatible;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedSchema;
import org.openapitools.openapidiff.core.model.DiffResult;
import org.openapitools.openapidiff.core.model.schema.ChangedRequired;
import org.openapitools.openapidiff.core.output.ConsoleRender;

public class SchemaDiffTest {

  @Test // issue #717
  public void schemaPropertyDefaultChanged() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/schema-props-defaults-handling-1.yaml",
            "schemaDiff/schema-props-defaults-handling-2.yaml");

    assertEquals(1, changedOpenApi.getChangedOperations().size());
    assertEquals(1, changedOpenApi.getChangedSchemas().size());
    ChangedSchema changedSchema = changedOpenApi.getChangedSchemas().get(0);
    assertEquals(1, changedSchema.getChangedProperties().size());
    assertTrue(changedSchema.getChangedProperties().containsKey("field1"));
    assertTrue(changedSchema.getChangedProperties().get("field1").isChangeDefault());
  }

  @Test
  public void schemaPropertyTypeChanged() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/schema-props-defaults-handling-1.yaml",
            "schemaDiff/schema-props-defaults-handling-2.yaml");
  }

  @Test // issue #485
  public void schemaBecomesDeprecatedTest() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/schema-deprecated-handling-1.yaml",
            "schemaDiff/schema-deprecated-handling-2.yaml");

    ChangedSchema requestBodySchema =
        getRequestBodyChangedSchema(
            changedOpenApi, POST, "/schema-diff/deprecated/added", "application/json");
    assertNotNull(requestBodySchema);
    assertTrue(requestBodySchema.isChangeDeprecated());
  }

  @Test // issue #485
  public void schemaBecomesNotDeprecatedTest() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/schema-deprecated-handling-1.yaml",
            "schemaDiff/schema-deprecated-handling-2.yaml");

    ChangedSchema requestBodySchema =
        getRequestBodyChangedSchema(
            changedOpenApi, POST, "/schema-diff/deprecated/removed", "application/json");
    assertNotNull(requestBodySchema);
    assertTrue(requestBodySchema.isChangeDeprecated());
  }

  @Test // issue #256
  void booleanAdditionalPropertiesAreSupported() {
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations(
            "schemaDiff/schema-additional-properties-1.json",
            "schemaDiff/schema-additional-properties-2.json");

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ConsoleRender render = new ConsoleRender();
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void addPropertyHandlingTest() {
    assertOpenApiBackwardCompatible(
        "schemaDiff/schema-add-property-1.yaml", "schemaDiff/schema-add-property-2.yaml", true);
  }

  @Test // issue #537
  public void addPropertyInPutApiIsCompatible() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/schema-add-property-put-1.yaml",
            "schemaDiff/schema-add-property-put-2.yaml");

    assertThat(changedOpenApi.isDifferent()).isTrue();
    assertThat(changedOpenApi.isCompatible()).isTrue();
  }

  @Test // issues #483 #463
  public void changeMultipleOfHandling() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/schema-multiple-of-diff-1.yaml",
            "schemaDiff/schema-multiple-of-diff-2.yaml");
    ChangedSchema changedSchema =
        getRequestBodyChangedSchema(
            changedOpenApi, POST, "/schema/numeric/multiple-of", "application/json");

    assertThat(changedSchema).isNotNull();
    Map<String, ChangedSchema> props = changedSchema.getChangedProperties();
    assertThat(props).isNotEmpty();

    // Check changes in multipleOf
    assertThat(props.get("field1").getMultipleOf().isIncompatible()).isTrue();
    assertThat(props.get("field1").getMultipleOf().getLeft()).isEqualTo(BigDecimal.valueOf(10));
    assertThat(props.get("field1").getMultipleOf().getRight()).isEqualTo(BigDecimal.valueOf(20));

    assertThat(props.get("field2").getMultipleOf().isIncompatible()).isTrue();
    assertThat(props.get("field2").getMultipleOf().getLeft()).isEqualTo(BigDecimal.valueOf(0.01));
    assertThat(props.get("field2").getMultipleOf().getRight()).isEqualTo(BigDecimal.valueOf(0.1));

    // Check addition of multipleOf
    assertThat(props.get("field3").getMultipleOf().isIncompatible()).isTrue();
    assertThat(props.get("field3").getMultipleOf().getLeft()).isNull();
    assertThat(props.get("field3").getMultipleOf().getRight()).isEqualTo(BigDecimal.valueOf(10));

    // Check deletion of multipleOf
    assertThat(props.get("field4").getMultipleOf().getLeft()).isEqualTo(BigDecimal.valueOf(10));
    assertThat(props.get("field4").getMultipleOf().getRight()).isNull();
  }

  @Test // issues #480
  public void changeMinMaxItemsHandling() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/schema-min-max-items-diff-1.yaml",
            "schemaDiff/schema-min-max-items-diff-2.yaml");
    ChangedSchema changedSchema =
        getRequestBodyChangedSchema(
            changedOpenApi, POST, "/schema/array/items", "application/json");

    assertThat(changedSchema).isNotNull();
    Map<String, ChangedSchema> props = changedSchema.getChangedProperties();
    assertThat(props).isNotEmpty();

    // Check increasing of minItems
    assertThat(props.get("field1").getMinItems().isIncompatible()).isTrue();
    assertThat(props.get("field1").getMinItems().getOldValue()).isEqualTo(1);
    assertThat(props.get("field1").getMinItems().getNewValue()).isEqualTo(2);

    // Check decreasing of minItems
    assertThat(props.get("field2").getMinItems().isCompatible()).isTrue();
    assertThat(props.get("field2").getMinItems().getOldValue()).isEqualTo(20);
    assertThat(props.get("field2").getMinItems().getNewValue()).isEqualTo(10);

    // Check increasing of maxItems
    assertThat(props.get("field3").getMaxItems().isCompatible()).isTrue();
    assertThat(props.get("field3").getMaxItems().getOldValue()).isEqualTo(90);
    assertThat(props.get("field3").getMaxItems().getNewValue()).isEqualTo(100);

    // Check decreasing of maxItems
    assertThat(props.get("field4").getMaxItems().isIncompatible()).isTrue();
    assertThat(props.get("field4").getMaxItems().getOldValue()).isEqualTo(100);
    assertThat(props.get("field4").getMaxItems().getNewValue()).isEqualTo(90);
  }

  @Test // issue #482
  public void changeNullableHandling() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/schema-nullable-diff-1.yaml", "schemaDiff/schema-nullable-diff-2.yaml");
    ChangedSchema changedSchema =
        getRequestBodyChangedSchema(changedOpenApi, POST, "/schema/nullable", "application/json");

    assertThat(changedSchema).isNotNull();
    Map<String, ChangedSchema> props = changedSchema.getChangedProperties();
    assertThat(props).isNotEmpty();

    // Check no changes in nullable
    assertThat(props.get("field0")).isNull();

    // Check changes in nullable
    assertThat(props.get("field1").getNullable().isCompatible()).isTrue();
    assertThat(props.get("field1").getNullable().getLeft()).isTrue();
    assertThat(props.get("field1").getNullable().getRight()).isFalse();

    // Check deletion of nullable
    assertThat(props.get("field2").getNullable().isCompatible()).isTrue();
    assertThat(props.get("field2").getNullable().getLeft()).isTrue();
    assertThat(props.get("field2").getNullable().getRight()).isNull();

    // Check addition of nullable
    assertThat(props.get("field3").getNullable().isCompatible()).isTrue();
    assertThat(props.get("field3").getNullable().getLeft()).isNull();
    assertThat(props.get("field3").getNullable().getRight()).isTrue();
  }

  @Test // issue #478
  public void changeUniqueItemsHandling() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/schema-uniqueItems-diff-1.yaml",
            "schemaDiff/schema-uniqueItems-diff-2.yaml");
    ChangedSchema changedSchema =
        getRequestBodyChangedSchema(
            changedOpenApi, POST, "/schema/uniqueItems", "application/json");

    assertThat(changedSchema).isNotNull();
    Map<String, ChangedSchema> props = changedSchema.getChangedProperties();
    assertThat(props).isNotEmpty();

    // Check no changes in uniqueItems
    assertThat(props.get("field0")).isNull();

    // Check changes true -> false
    assertThat(props.get("field1").getUniqueItems().isCompatible()).isTrue();
    assertThat(props.get("field1").getUniqueItems().getLeft()).isTrue();
    assertThat(props.get("field1").getUniqueItems().getRight()).isFalse();

    // Check changes false -> true
    assertThat(props.get("field2").getUniqueItems().isIncompatible()).isTrue();
    assertThat(props.get("field2").getUniqueItems().getLeft()).isFalse();
    assertThat(props.get("field2").getUniqueItems().getRight()).isTrue();

    // Check deletion of uniqueItems
    assertThat(props.get("field3").getUniqueItems().isCompatible()).isTrue();
    assertThat(props.get("field3").getUniqueItems().getLeft()).isTrue();
    assertThat(props.get("field3").getUniqueItems().getRight()).isNull();

    // Check addition of uniqueItems
    assertThat(props.get("field4").getUniqueItems().isIncompatible()).isTrue();
    assertThat(props.get("field4").getUniqueItems().getLeft()).isNull();
    assertThat(props.get("field4").getUniqueItems().getRight()).isTrue();
  }

  @Test // issue #479
  public void changeMinMaxPropertiesHandling() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/schema-min-max-properties-diff-1.yaml",
            "schemaDiff/schema-min-max-properties-diff-2.yaml");
    ChangedSchema changedSchema =
        getRequestBodyChangedSchema(
            changedOpenApi, POST, "/schema/object/min-max-properties", "application/json");

    assertThat(changedSchema).isNotNull();
    Map<String, ChangedSchema> props = changedSchema.getChangedProperties();
    assertThat(props).isNotEmpty();

    // Check increasing of minProperties
    assertThat(props.get("field1").getMinProperties().isIncompatible()).isTrue();
    assertThat(props.get("field1").getMinProperties().getOldValue()).isEqualTo(1);
    assertThat(props.get("field1").getMinProperties().getNewValue()).isEqualTo(10);

    // Check decreasing of minProperties
    assertThat(props.get("field2").getMinProperties().isCompatible()).isTrue();
    assertThat(props.get("field2").getMinProperties().getOldValue()).isEqualTo(10);
    assertThat(props.get("field2").getMinProperties().getNewValue()).isEqualTo(1);

    // Check increasing of maxProperties
    assertThat(props.get("field3").getMaxProperties().isCompatible()).isTrue();
    assertThat(props.get("field3").getMaxProperties().getOldValue()).isEqualTo(10);
    assertThat(props.get("field3").getMaxProperties().getNewValue()).isEqualTo(100);

    // Check decreasing of maxProperties
    assertThat(props.get("field4").getMaxProperties().isIncompatible()).isTrue();
    assertThat(props.get("field4").getMaxProperties().getOldValue()).isEqualTo(100);
    assertThat(props.get("field4").getMaxProperties().getNewValue()).isEqualTo(10);
  }

  @Test // issue #346
  public void requiredPropertyChangedTest() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/required-property-1.yaml", "schemaDiff/required-property-2.yaml");

    // Test request body schema changes
    ChangedSchema requestSchema =
        getRequestBodyChangedSchema(changedOpenApi, POST, "/schema/required", "application/json");

    assertNotNull(requestSchema, "Request schema should not be null");
    ChangedRequired requestRequired = requestSchema.getRequired();
    assertNotNull(requestRequired, "Request required changes should not be null");

    // Test request property changes
    assertThat(requestRequired.getIncreased()).contains("becomesRequired");
    assertThat(requestRequired.getMissing()).contains("becomesOptional");
    assertThat(requestRequired.getIncreased()).doesNotContain("unchanged");
    assertThat(requestRequired.getMissing()).doesNotContain("unchanged");

    // Verify breaking changes in request - new required properties are breaking
    assertThat(requestRequired.isItemsChanged()).isEqualTo(DiffResult.INCOMPATIBLE);

    // Test response schema changes
    ChangedSchema responseSchema =
        getResponseBodyChangedSchema(
            changedOpenApi, POST, "/schema/required", "200", "application/json");

    assertNotNull(responseSchema, "Response schema should not be null");
    ChangedRequired responseRequired = responseSchema.getRequired();
    assertNotNull(responseRequired, "Response required changes should not be null");

    // Test response property changes
    assertThat(responseRequired.getIncreased()).contains("becomesRequired");
    assertThat(responseRequired.getMissing()).contains("becomesOptional");
    assertThat(responseRequired.getIncreased()).doesNotContain("unchanged");
    assertThat(responseRequired.getMissing()).doesNotContain("unchanged");

    // Verify breaking changes in response - removing required properties is breaking
    assertThat(responseRequired.isItemsChanged()).isEqualTo(DiffResult.INCOMPATIBLE);
  }

  @Test // issue #104
  public void changePatternHandling() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/schema-pattern-diff-1.yaml", "schemaDiff/schema-pattern-diff-2.yaml");
    ChangedSchema changedSchema =
        getRequestBodyChangedSchema(changedOpenApi, POST, "/schema/pattern", "application/json");

    assertThat(changedSchema).isNotNull();
    Map<String, ChangedSchema> props = changedSchema.getChangedProperties();
    assertThat(props).isNotEmpty();

    // Check no changes in pattern
    assertThat(props.get("patternUnchanged")).isNull();

    // Check changes in pattern (modification)
    assertThat(props.get("patternModified").getPattern().isChanged())
        .isEqualTo(DiffResult.INCOMPATIBLE);
    assertThat(props.get("patternModified").getPattern().getOldPattern()).isEqualTo("^[a-z]+$");
    assertThat(props.get("patternModified").getPattern().getNewPattern()).isEqualTo("^[a-zA-Z]+$");

    // Check deletion of pattern
    assertThat(props.get("patternRemoved").getPattern().isChanged())
        .isEqualTo(DiffResult.INCOMPATIBLE);
    assertThat(props.get("patternRemoved").getPattern().getOldPattern()).isEqualTo("^[0-9]+$");
    assertThat(props.get("patternRemoved").getPattern().getNewPattern()).isNull();

    // Check addition of pattern
    assertThat(props.get("patternAdded").getPattern().isChanged())
        .isEqualTo(DiffResult.INCOMPATIBLE);
    assertThat(props.get("patternAdded").getPattern().getOldPattern()).isNull();
    assertThat(props.get("patternAdded").getPattern().getNewPattern())
        .isEqualTo("^\\d{3}-\\d{2}-\\d{4}$?");
  }

  @Test // issue #212
  public void testAnyOfDiff() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/anyOf-diff-1.yaml", "schemaDiff/anyOf-diff-2.yaml");
    ChangedSchema changedSchema =
        getRequestBodyChangedSchema(changedOpenApi, POST, "/anyof/test", "application/json");

    assertThat(changedSchema).isNotNull();
    // The diff compares the *merged* schema resulting from anyOf, not the anyOf structure itself.
    // See details in #772
    assertThat(changedSchema.isChanged()).isEqualTo(DiffResult.COMPATIBLE);

    // fieldA only changed required status, not its schema
    assertThat(changedSchema.getChangedProperties()).isEmpty();

    // fieldB: Removed from the merged schema properties
    assertThat(changedSchema.getMissingProperties()).containsKey("fieldB");

    // fieldC: Added to the merged schema properties
    assertThat(changedSchema.getIncreasedProperties()).containsKey("fieldC");

    // Check the overall required list changes for the merged schema
    assertThat(changedSchema.getRequired().isChanged()).isEqualTo(DiffResult.COMPATIBLE);
    assertThat(changedSchema.getRequired().getMissing()).containsExactly("fieldA");
    assertThat(changedSchema.getRequired().getIncreased()).isEmpty();
  }

  @Test // issue #212 - adapted for allOf
  public void testAllOfDiff() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/allOf-diff-1.yaml", "schemaDiff/allOf-diff-2.yaml");
    ChangedSchema changedSchema =
        getRequestBodyChangedSchema(changedOpenApi, POST, "/allof/test", "application/json");

    assertThat(changedSchema).isNotNull();
    // The diff compares the *merged* schema resulting from allOf, not the allOf structure itself.
    // See details in #772
    assertThat(changedSchema.isChanged()).isEqualTo(DiffResult.COMPATIBLE);

    // fieldA only changed required status, commonField is unchanged
    assertThat(changedSchema.getChangedProperties()).isEmpty();

    // fieldB: Removed from the merged schema properties
    assertThat(changedSchema.getMissingProperties()).containsKey("fieldB");

    // fieldC: Added to the merged schema properties
    assertThat(changedSchema.getIncreasedProperties()).containsKey("fieldC");

    // Check the overall required list changes for the merged schema
    assertThat(changedSchema.getRequired().isChanged()).isEqualTo(DiffResult.COMPATIBLE);
    assertThat(changedSchema.getRequired().getMissing()).containsExactly("fieldA");
    assertThat(changedSchema.getRequired().getIncreased()).isEmpty();
  }
}
