package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openapitools.openapidiff.core.ChangesResolver.getChangedOperation;
import static org.openapitools.openapidiff.core.ChangesResolver.getRequestBodyChangedSchema;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardCompatible;

import io.swagger.v3.oas.models.PathItem.HttpMethod;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;
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

  @Test // issue #485
  public void schemaBecomesDeprecatedTest() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/schema-deprecated-handling-1.yaml",
            "schemaDiff/schema-deprecated-handling-2.yaml");

    ChangedOperation operation =
        getChangedOperation(changedOpenApi, HttpMethod.POST, "/schema-diff/deprecated/added");
    assertNotNull(operation);

    ChangedSchema requestBodySchema = getRequestBodyChangedSchema(operation, "application/json");
    assertNotNull(requestBodySchema);
    assertTrue(requestBodySchema.isChangeDeprecated());
  }

  @Test // issue #485
  public void schemaBecomesNotDeprecatedTest() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "schemaDiff/schema-deprecated-handling-1.yaml",
            "schemaDiff/schema-deprecated-handling-2.yaml");

    ChangedOperation operation =
        getChangedOperation(changedOpenApi, HttpMethod.POST, "/schema-diff/deprecated/removed");
    assertNotNull(operation);

    ChangedSchema requestBodySchema = getRequestBodyChangedSchema(operation, "application/json");
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
}
