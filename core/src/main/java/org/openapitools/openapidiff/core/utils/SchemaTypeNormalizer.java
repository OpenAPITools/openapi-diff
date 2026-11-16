package org.openapitools.openapidiff.core.utils;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Mirrors the JSON Schema 2020-12 {@code types} set of a parsed schema back into the singular
 * {@code type} keyword when it holds exactly one type.
 *
 * <p>For OpenAPI 3.1 documents the parser stores the {@code type} keyword in {@link
 * Schema#getTypes()} and leaves {@link Schema#getType()} null, while OpenAPI 3.0 documents expose
 * the same keyword only through {@link Schema#getType()}. Comparing a 3.0 document against a 3.1
 * document therefore reports every identical schema as a type change (see issue 858).
 *
 * <p>A single-element {@code types} set and the equivalent singular {@code type} denote the same
 * schema, so this normalization is lossless for those. Sets with zero or multiple types are left
 * untouched because they do not correspond to any singular {@code type} value.
 */
public final class SchemaTypeNormalizer {

  private SchemaTypeNormalizer() {}

  /** Walks the whole document and mirrors single-element {@code types} sets into {@code type}. */
  public static void normalize(OpenAPI openAPI) {
    if (openAPI == null) {
      return;
    }
    // Identity-based visited set: schemas can be shared and recursive.
    Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
    walkPathItems(openAPI.getPaths(), visited);
    walkPathItems(openAPI.getWebhooks(), visited);

    Components components = openAPI.getComponents();
    if (components == null) {
      return;
    }
    walkSchemas(components.getSchemas(), visited);
    if (components.getParameters() != null) {
      components.getParameters().values().forEach(parameter -> walkParameter(parameter, visited));
    }
    if (components.getRequestBodies() != null) {
      components
          .getRequestBodies()
          .values()
          .forEach(requestBody -> walkRequestBody(requestBody, visited));
    }
    if (components.getResponses() != null) {
      components
          .getResponses()
          .values()
          .forEach(apiResponse -> walkApiResponse(apiResponse, visited));
    }
    if (components.getHeaders() != null) {
      components.getHeaders().values().forEach(header -> walkSchema(header.getSchema(), visited));
    }
    walkPathItems(components.getPathItems(), visited);
    if (components.getCallbacks() != null) {
      components.getCallbacks().values().forEach(callback -> walkPathItems(callback, visited));
    }
  }

  private static void walkPathItems(Map<String, PathItem> pathItems, Set<Object> visited) {
    if (pathItems != null) {
      pathItems.values().forEach(pathItem -> walkPathItem(pathItem, visited));
    }
  }

  private static void walkPathItem(PathItem pathItem, Set<Object> visited) {
    if (pathItem == null || !visited.add(pathItem)) {
      return;
    }
    pathItem.readOperations().forEach(operation -> walkOperation(operation, visited));
  }

  private static void walkOperation(Operation operation, Set<Object> visited) {
    if (operation == null) {
      return;
    }
    if (operation.getParameters() != null) {
      operation.getParameters().forEach(parameter -> walkParameter(parameter, visited));
    }
    walkRequestBody(operation.getRequestBody(), visited);
    if (operation.getResponses() != null) {
      operation.getResponses().values().forEach(response -> walkApiResponse(response, visited));
    }
    if (operation.getCallbacks() != null) {
      operation.getCallbacks().values().forEach(callback -> walkPathItems(callback, visited));
    }
  }

  private static void walkParameter(Parameter parameter, Set<Object> visited) {
    if (parameter == null) {
      return;
    }
    walkSchema(parameter.getSchema(), visited);
    walkContent(parameter.getContent(), visited);
  }

  private static void walkRequestBody(RequestBody requestBody, Set<Object> visited) {
    if (requestBody == null) {
      return;
    }
    walkContent(requestBody.getContent(), visited);
  }

  private static void walkApiResponse(ApiResponse apiResponse, Set<Object> visited) {
    if (apiResponse == null) {
      return;
    }
    walkContent(apiResponse.getContent(), visited);
    if (apiResponse.getHeaders() != null) {
      apiResponse.getHeaders().values().forEach(header -> walkSchema(header.getSchema(), visited));
    }
  }

  private static void walkContent(Content content, Set<Object> visited) {
    if (content == null) {
      return;
    }
    for (MediaType mediaType : content.values()) {
      if (mediaType != null) {
        walkSchema(mediaType.getSchema(), visited);
      }
    }
  }

  private static void walkSchemas(Map<String, Schema> schemas, Set<Object> visited) {
    if (schemas != null) {
      schemas.values().forEach(schema -> walkSchema(schema, visited));
    }
  }

  private static void walkSchema(Schema<?> schema, Set<Object> visited) {
    if (schema == null || !visited.add(schema)) {
      return;
    }
    normalizeType(schema);

    if (schema.getProperties() != null) {
      schema.getProperties().values().forEach(property -> walkSchema(property, visited));
    }
    walkSchema(schema.getItems(), visited);
    walkSchema(schema.getNot(), visited);
    walkSchema(schema.getAdditionalItems(), visited);
    walkSchema(schema.getContains(), visited);
    walkSchema(schema.getIf(), visited);
    walkSchema(schema.getThen(), visited);
    walkSchema(schema.getElse(), visited);
    if (schema.getAdditionalProperties() instanceof Schema) {
      walkSchema((Schema<?>) schema.getAdditionalProperties(), visited);
    }
    if (schema.getAllOf() != null) {
      schema.getAllOf().forEach(item -> walkSchema(item, visited));
    }
    if (schema.getAnyOf() != null) {
      schema.getAnyOf().forEach(item -> walkSchema(item, visited));
    }
    if (schema.getOneOf() != null) {
      schema.getOneOf().forEach(item -> walkSchema(item, visited));
    }
    if (schema.getPrefixItems() != null) {
      schema.getPrefixItems().forEach(item -> walkSchema(item, visited));
    }
  }

  private static void normalizeType(Schema<?> schema) {
    if (schema.getType() == null && schema.getTypes() != null && schema.getTypes().size() == 1) {
      schema.setType(schema.getTypes().iterator().next());
    }
  }
}
