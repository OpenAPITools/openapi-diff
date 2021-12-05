package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.slf4j.LoggerFactory.getLogger;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.slf4j.Logger;

public class LargeSchemaTest {
  public static final Logger LOG = getLogger(LargeSchemaTest.class);

  @Test
  public void testGeneratedApiSame() {
    OpenAPI generated = largeGeneratedApi();
    assertOpenApiAreEquals(generated, generated);
  }

  @Test
  public void testGeneratedApiDifferent() {
    OpenAPI generated = largeGeneratedApi();
    OpenAPI generated2 = largeGeneratedApi();
    assertOpenApiBackwardIncompatible(generated, generated2);
  }

  public static void assertOpenApiAreEquals(OpenAPI oldSpec, OpenAPI newSpec) {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromSpecifications(oldSpec, newSpec);
    LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
    assertThat(changedOpenApi.getNewEndpoints()).isEmpty();
    assertThat(changedOpenApi.getMissingEndpoints()).isEmpty();
    assertThat(changedOpenApi.getChangedOperations()).isEmpty();
  }

  public static void assertOpenApiBackwardIncompatible(OpenAPI oldSpec, OpenAPI newSpec) {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromSpecifications(oldSpec, newSpec);
    LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
    assertThat(changedOpenApi.isDifferent()).isTrue();
    assertThat(changedOpenApi.isIncompatible()).isTrue();
  }

  public OpenAPI largeGeneratedApi() {
    final int modelCount = 200;
    final int arrayCount = 50;
    final int refPerModelCount = 5;
    final int endpointCount = 20;

    Random random = new Random();

    OpenAPI api = new OpenAPI();
    api.setPaths(new Paths());
    api.setComponents(new Components());

    // create 200 model schemas
    Map<String, Schema> schemas = new LinkedHashMap<>();
    api.getComponents().setSchemas(schemas);
    for (int i = 0; i < modelCount; i++) {
      ObjectSchema schema = new ObjectSchema();
      schema.setProperties(new LinkedHashMap<>());
      schema.getProperties().put("name", new StringSchema());
      schema.getProperties().put("description", new StringSchema());
      List<String> required = new ArrayList<>();
      required.add("name");
      schema.setRequired(required);
      schemas.put(modelName(i), schema);
    }

    // create 50 array schemas
    for (int i = modelCount; i < modelCount + arrayCount; i++) {
      ArraySchema arraySchema = new ArraySchema();
      arraySchema.setItems(refSchema(i));
      schemas.put(modelName(i), arraySchema);
    }

    // list of schema names

    // Create cyclic properties on schemas, make the refs required
    schemas.values().stream()
        .filter(schema -> schema instanceof ObjectSchema)
        .map(schema -> (ObjectSchema) schema)
        .forEach(
            schema -> {
              for (int i = 0; i < refPerModelCount; i++) {
                int schemaNumber = random.nextInt(modelCount + arrayCount);
                String propertyName = "refTo" + schemaNumber;
                schema.getProperties().put(propertyName, refSchema(schemaNumber));
                schema.getRequired().add(propertyName);
              }
            });

    // generated endpoints
    for (int i = 0; i < endpointCount; i++) {
      String path = "/endpoint" + i;
      PathItem pathItem = new PathItem();
      Operation operation = new Operation();
      pathItem.post(operation);

      operation.setRequestBody(
          new RequestBody()
              .content(
                  new Content()
                      .addMediaType(
                          "application/json", new MediaType().schema(refSchema(i % modelCount)))));
      ApiResponse responseOk =
          new ApiResponses()
              .put(
                  "200",
                  new ApiResponse()
                      .content(
                          new Content()
                              .addMediaType(
                                  "application/json",
                                  new MediaType().schema(refSchema(i % modelCount)))));
      ApiResponses responses = new ApiResponses();
      responses.put("200", responseOk);
      operation.setResponses(responses);
      api.getPaths().put(path, pathItem);
    }

    try {
      LOG.info("Printing schema to target/large-api.yaml");
      Yaml.pretty().writeValue(new File("target/large-api.yaml"), api);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return api;
  }

  private Schema refSchema(final int i) {
    String itemModelName = modelName(i);
    Schema refSchema = new Schema();
    refSchema.set$ref("#/components/schemas/" + itemModelName);
    return refSchema;
  }

  private String modelName(final int i) {
    return String.format("Model%03d", i);
  }
}
