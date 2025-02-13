package org.openapitools.openapidiff.core;

import static io.swagger.v3.oas.models.PathItem.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openapitools.openapidiff.core.ChangesResolver.getRequestBodyChangedSchema;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardCompatible;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedSchema;
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
    assertThat(props.get("field4").getMultipleOf().isCompatible()).isTrue();
    assertThat(props.get("field4").getMultipleOf().getLeft()).isEqualTo(BigDecimal.valueOf(10));
    assertThat(props.get("field4").getMultipleOf().getRight()).isNull();
  }

  @Test // issue #482
  public void changeNullabeHandling() {
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
}
