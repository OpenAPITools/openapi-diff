package org.openapitools.openapidiff.core.output;

import static java.lang.String.format;
import static org.openapitools.openapidiff.core.model.Changed.result;
import static org.openapitools.openapidiff.core.utils.ChangedUtils.isUnchanged;

import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.openapidiff.core.model.*;
import org.openapitools.openapidiff.core.utils.RefPointer;
import org.openapitools.openapidiff.core.utils.RefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkdownRender implements Render {
  private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownRender.class);
  private static final String H3 = "### ";
  private static final String H4 = "#### ";
  private static final String H5 = "##### ";
  private static final String H6 = "###### ";
  private static final String BLOCKQUOTE = "> ";
  private static final String CODE = "`";
  private static final String PRE_CODE = "    ";
  private static final String PRE_LI = "    ";
  private static final String LI = "* ";
  private static final String HR = "---\n";

  protected RefPointer<Schema<?>> refPointer = new RefPointer<>(RefType.SCHEMAS);
  protected ChangedOpenApi diff;
  /**
   * A parameter which indicates whether or not metadata (summary and metadata) changes should be
   * logged in the changelog file.
   */
  protected boolean showChangedMetadata;

  public String render(ChangedOpenApi diff) {
    this.diff = diff;
    return listEndpoints("What's New", diff.getNewEndpoints())
        + listEndpoints("What's Deleted", diff.getMissingEndpoints())
        + listEndpoints("What's Deprecated", diff.getDeprecatedEndpoints())
        + listEndpoints(diff.getChangedOperations());
  }

  protected String sectionTitle(String title) {
    return H4 + title + '\n' + HR + '\n';
  }

  protected String listEndpoints(String title, List<Endpoint> endpoints) {
    if (null == endpoints || endpoints.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder(sectionTitle(title));
    endpoints.stream()
        .map(e -> itemEndpoint(e.getMethod().toString(), e.getPathUrl(), e.getSummary()))
        .forEach(sb::append);
    return sb.toString();
  }

  protected String itemEndpoint(String method, String path, String summary) {
    return H5 + CODE + method + CODE + " " + path + "\n\n" + metadata(summary) + "\n";
  }

  protected String itemEndpoint(String method, String path, ChangedMetadata summary) {
    return H5 + CODE + method + CODE + " " + path + "\n\n" + metadata("summary", summary) + "\n";
  }

  protected String titleH5(String title) {
    return H6 + title + '\n';
  }

  protected String listEndpoints(List<ChangedOperation> changedOperations) {
    if (null == changedOperations || changedOperations.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder(sectionTitle("What's Changed"));
    changedOperations.stream()
        .map(
            operation -> {
              StringBuilder details =
                  new StringBuilder()
                      .append(
                          itemEndpoint(
                              operation.getHttpMethod().toString(),
                              operation.getPathUrl(),
                              operation.getSummary()));
              if (result(operation.getParameters()).isDifferent()) {
                details
                    .append(titleH5("Parameters:"))
                    .append(parameters(operation.getParameters()));
              }
              if (operation.resultRequestBody().isDifferent()) {
                details
                    .append(titleH5("Request:"))
                    .append(metadata("Description", operation.getRequestBody().getDescription()))
                    .append(bodyContent(operation.getRequestBody().getContent()));
              }
              if (operation.resultApiResponses().isDifferent()) {
                details
                    .append(titleH5("Return Type:"))
                    .append(responses(operation.getApiResponses()));
              }
              return details.toString();
            })
        .forEach(sb::append);
    return sb.toString();
  }

  protected String responses(ChangedApiResponse changedApiResponse) {
    StringBuilder sb = new StringBuilder("\n");
    sb.append(listResponse("New response", changedApiResponse.getIncreased()));
    sb.append(listResponse("Deleted response", changedApiResponse.getMissing()));
    changedApiResponse.getChanged().entrySet().stream()
        .map(e -> this.itemResponse(e.getKey(), e.getValue()))
        .forEach(sb::append);
    return sb.toString();
  }

  protected String listResponse(String title, Map<String, ApiResponse> responses) {
    StringBuilder sb = new StringBuilder();
    responses.entrySet().stream()
        .map(e -> this.itemResponse(title, e.getKey(), e.getValue()))
        .forEach(sb::append);
    return sb.toString();
  }

  protected String itemResponse(String title, String code, ApiResponse response) {
    return this.itemResponse(title, code, response.getDescription());
  }

  protected String itemResponse(String code, ChangedResponse response) {
    StringBuilder sb = new StringBuilder();
    sb.append(
        this.itemResponse(
            "Changed response",
            code,
            null == response.getNewApiResponse()
                ? ""
                : response.getNewApiResponse().getDescription()));
    sb.append(headers(response.getHeaders()));
    if (response.getContent() != null) {
      sb.append(this.bodyContent(LI, response.getContent()));
    }
    return sb.toString();
  }

  protected String itemResponse(String title, String code, String description) {
    StringBuilder sb = new StringBuilder();
    String status = "";
    if (!code.equals("default")) {
      status = HttpStatus.getReasonPhrase(Integer.parseInt(code));
    }
    sb.append(format("%s : **%s %s**\n", title, code, status));
    sb.append(metadata(description));
    return sb.toString();
  }

  protected String headers(ChangedHeaders headers) {
    StringBuilder sb = new StringBuilder();
    if (headers != null) {
      sb.append(listHeader("New header", headers.getIncreased()))
          .append(listHeader("Deleted header", headers.getMissing()));
      headers.getChanged().entrySet().stream()
          .map(e -> this.itemHeader(e.getKey(), e.getValue()))
          .forEach(sb::append);
    }
    return sb.toString();
  }

  protected String listHeader(String title, Map<String, Header> headers) {
    StringBuilder sb = new StringBuilder();
    headers.entrySet().stream()
        .map(e -> this.itemHeader(title, e.getKey(), e.getValue()))
        .forEach(sb::append);
    return sb.toString();
  }

  protected String itemHeader(String title, String name, Header header) {
    return this.itemHeader(title, name, header.getDescription());
  }

  protected String itemHeader(String code, ChangedHeader header) {
    return this.itemHeader(
        "Changed header",
        code,
        null == header.getNewHeader() ? "" : header.getNewHeader().getDescription());
  }

  protected String itemHeader(String title, String mediaType, String description) {
    return format("%s : `%s`\n\n", title, mediaType) + metadata(description) + '\n';
  }

  protected String bodyContent(String prefix, ChangedContent changedContent) {
    if (changedContent == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder("\n");
    sb.append(listContent(prefix, "New content type", changedContent.getIncreased()));
    sb.append(listContent(prefix, "Deleted content type", changedContent.getMissing()));
    final int deepness;
    if (StringUtils.isNotBlank(prefix)) {
      deepness = 1;
    } else {
      deepness = 0;
    }
    changedContent.getChanged().entrySet().stream()
        .map(e -> this.itemContent(deepness, e.getKey(), e.getValue()))
        .forEach(e -> sb.append(prefix).append(e));
    return sb.toString();
  }

  protected String bodyContent(ChangedContent changedContent) {
    return bodyContent("", changedContent);
  }

  protected String listContent(String prefix, String title, Map<String, MediaType> mediaTypes) {
    StringBuilder sb = new StringBuilder();
    mediaTypes.entrySet().stream()
        .map(e -> this.itemContent(title, e.getKey(), e.getValue()))
        .forEach(e -> sb.append(prefix).append(e));
    return sb.toString();
  }

  protected String itemContent(String title, String mediaType) {
    return format("%s : `%s`\n\n", title, mediaType);
  }

  protected String itemContent(String title, String mediaType, MediaType content) {
    return itemContent(title, mediaType);
  }

  protected String itemContent(int deepness, String mediaType, ChangedMediaType content) {
    return itemContent("Changed content type", mediaType) + schema(deepness, content.getSchema());
  }

  protected String schema(ChangedSchema schema) {
    return schema(1, schema);
  }

  protected String oneOfSchema(int deepness, ChangedOneOfSchema schema, String discriminator) {
    StringBuilder sb = new StringBuilder();
    schema
        .getMissing()
        .keySet()
        .forEach(
            key -> sb.append(format("%sDeleted '%s' %s\n", indent(deepness), key, discriminator)));
    schema
        .getIncreased()
        .forEach(
            (key, sub) ->
                sb.append(format("%sAdded '%s' %s:\n", indent(deepness), key, discriminator))
                    .append(schema(deepness, sub, schema.getContext())));
    schema
        .getChanged()
        .forEach(
            (key, sub) ->
                sb.append(format("%sUpdated `%s` %s:\n", indent(deepness), key, discriminator))
                    .append(schema(deepness, sub)));
    return sb.toString();
  }

  protected String required(int deepness, String title, List<String> required) {
    StringBuilder sb = new StringBuilder();
    if (!required.isEmpty()) {
      sb.append(format("%s%s:\n", indent(deepness), title));
      required.forEach(s -> sb.append(format("%s- `%s`\n", indent(deepness), s)));
      sb.append("\n");
    }
    return sb.toString();
  }

  protected String schema(int deepness, ChangedSchema schema) {
    StringBuilder sb = new StringBuilder();
    if (schema.isDiscriminatorPropertyChanged()) {
      LOGGER.debug("Discriminator property changed");
    }
    if (schema.getOneOfSchema() != null) {
      String discriminator =
          schema.getNewSchema().getDiscriminator() != null
              ? schema.getNewSchema().getDiscriminator().getPropertyName()
              : "";
      sb.append(oneOfSchema(deepness, schema.getOneOfSchema(), discriminator));
    }
    if (schema.getRequired() != null) {
      sb.append(required(deepness, "New required properties", schema.getRequired().getIncreased()));
      sb.append(required(deepness, "New optional properties", schema.getRequired().getMissing()));
    }
    if (schema.getItems() != null) {
      sb.append(items(deepness, schema.getItems()));
    }
    sb.append(listDiff(deepness, "enum", schema.getEnumeration()));
    sb.append(
        properties(
            deepness,
            "Added property",
            schema.getIncreasedProperties(),
            true,
            schema.getContext()));
    sb.append(
        properties(
            deepness,
            "Deleted property",
            schema.getMissingProperties(),
            false,
            schema.getContext()));
    schema
        .getChangedProperties()
        .forEach((name, property) -> sb.append(property(deepness, name, property)));
    return sb.toString();
  }

  protected String schema(int deepness, ComposedSchema schema, DiffContext context) {
    StringBuilder sb = new StringBuilder();
    if (schema.getAllOf() != null) {
      LOGGER.debug("All of schema");
      schema.getAllOf().stream()
          .map(this::resolve)
          .forEach(composedChild -> sb.append(schema(deepness, composedChild, context)));
    }
    if (schema.getOneOf() != null) {
      LOGGER.debug("One of schema");
      sb.append(format("%sOne of:\n\n", indent(deepness)));
      schema.getOneOf().stream()
          .map(this::resolve)
          .forEach(composedChild -> sb.append(schema(deepness + 1, composedChild, context)));
    }
    return sb.toString();
  }

  protected String schema(int deepness, Schema schema, DiffContext context) {
    StringBuilder sb = new StringBuilder();
    sb.append(listItem(deepness, "Enum", schema.getEnum()));
    sb.append(properties(deepness, "Property", schema.getProperties(), true, context));
    if (schema instanceof ComposedSchema) {
      sb.append(schema(deepness, (ComposedSchema) schema, context));
    } else if (schema instanceof ArraySchema) {
      sb.append(items(deepness, resolve(((ArraySchema) schema).getItems()), context));
    }
    return sb.toString();
  }

  protected String items(int deepness, ChangedSchema schema) {
    StringBuilder sb = new StringBuilder();
    String type = type(schema.getNewSchema());
    if (schema.isChangedType()) {
      type = type(schema.getOldSchema()) + " -> " + type(schema.getNewSchema());
    }
    sb.append(items(deepness, "Changed items", type, schema.getNewSchema().getDescription()));
    sb.append(schema(deepness, schema));
    return sb.toString();
  }

  protected String items(int deepness, Schema<?> schema, DiffContext context) {
    return items(deepness, "Items", type(schema), schema.getDescription())
        + schema(deepness, schema, context);
  }

  protected String items(int deepness, String title, String type, String description) {
    return format(
        "%s%s (%s):" + "\n%s\n",
        indent(deepness), title, type, metadata(indent(deepness + 1), description));
  }

  protected String properties(
      final int deepness,
      String title,
      Map<String, Schema<?>> properties,
      boolean showContent,
      DiffContext context) {
    StringBuilder sb = new StringBuilder();
    if (properties != null) {
      properties.forEach(
          (key, value) -> {
            sb.append(resolveProperty(deepness, value, key, title));
            if (showContent) {
              sb.append(schema(deepness + 1, resolve(value), context));
            }
          });
    }
    return sb.toString();
  }

  private String resolveProperty(int deepness, Schema<?> value, String key, String title) {
    try {
      return property(deepness, title, key, resolve(value));
    } catch (Exception e) {
      return property(deepness, title, key, type(value), "");
    }
  }

  protected String property(int deepness, String name, ChangedSchema schema) {
    StringBuilder sb = new StringBuilder();
    String type = type(schema.getNewSchema());
    if (schema.isChangedType()) {
      type = type(schema.getOldSchema()) + " -> " + type(schema.getNewSchema());
    }
    sb.append(
        property(deepness, "Changed property", name, type, schema.getNewSchema().getDescription()));
    sb.append(schema(++deepness, schema));
    return sb.toString();
  }

  protected String property(int deepness, String title, String name, Schema<?> schema) {
    return property(deepness, title, name, type(schema), schema.getDescription());
  }

  protected String property(
      int deepness, String title, String name, String type, String description) {
    return format(
        "%s* %s `%s` (%s)\n%s\n",
        indent(deepness), title, name, type, metadata(indent(deepness + 1), description));
  }

  protected String listDiff(int deepness, String name, ChangedList<?> listDiff) {
    if (listDiff == null) {
      return "";
    }
    return listItem(deepness, "Added " + name, listDiff.getIncreased())
        + listItem(deepness, "Removed " + name, listDiff.getMissing());
  }

  protected <T> String listItem(int deepness, String name, List<T> list) {
    StringBuilder sb = new StringBuilder();
    if (list != null && !list.isEmpty()) {
      sb.append(format("%s%s value%s:\n\n", indent(deepness), name, list.size() > 1 ? "s" : ""));
      list.forEach(p -> sb.append(format("%s* `%s`\n", indent(deepness), p)));
    }
    return sb.toString();
  }

  protected String parameters(ChangedParameters changedParameters) {
    List<ChangedParameter> changed = changedParameters.getChanged();
    StringBuilder sb = new StringBuilder("\n");
    sb.append(listParameter("Added", changedParameters.getIncreased()))
        .append(listParameter("Deleted", changedParameters.getMissing()));
    changed.stream().map(this::itemParameter).forEach(sb::append);
    return sb.toString();
  }

  protected String listParameter(String title, List<Parameter> parameters) {
    StringBuilder sb = new StringBuilder();
    parameters.stream().map(p -> itemParameter(title, p)).forEach(sb::append);
    return sb.toString();
  }

  protected String itemParameter(String title, Parameter parameter) {
    return this.itemParameter(
        title, parameter.getName(), parameter.getIn(), parameter.getDescription());
  }

  protected String itemParameter(String title, String name, String in, String description) {
    return format("%s: ", title)
        + code(name)
        + " in "
        + code(in)
        + '\n'
        + metadata(description)
        + '\n';
  }

  protected String itemParameter(ChangedParameter param) {
    Parameter rightParam = param.getNewParameter();
    if (param.isDeprecated()) {
      return itemParameter(
          "Deprecated", rightParam.getName(), rightParam.getIn(), rightParam.getDescription());
    }
    return itemParameter(
        "Changed", rightParam.getName(), rightParam.getIn(), rightParam.getDescription());
  }

  protected String code(String string) {
    return CODE + string + CODE;
  }

  protected String metadata(String name, ChangedMetadata changedMetadata) {
    return metadata("", name, changedMetadata);
  }

  protected String metadata(String beginning, String name, ChangedMetadata changedMetadata) {
    if (changedMetadata == null) {
      return "";
    }
    if (!isUnchanged(changedMetadata) && showChangedMetadata) {
      return format(
          "Changed %s:\n%s\nto:\n%s\n\n",
          name,
          metadata(beginning, changedMetadata.getLeft()),
          metadata(beginning, changedMetadata.getRight()));
    } else {
      return metadata(beginning, name, changedMetadata.getRight());
    }
  }

  protected String metadata(String metadata) {
    return metadata("", metadata);
  }

  protected String metadata(String beginning, String name, String metadata) {
    if (StringUtils.isBlank(metadata)) {
      return "";
    }
    return blockquote(beginning, metadata);
  }

  protected String metadata(String beginning, String metadata) {
    if (StringUtils.isBlank(metadata)) {
      return "";
    }
    return blockquote(beginning, metadata);
  }

  protected String blockquote(String beginning) {
    return beginning + BLOCKQUOTE;
  }

  protected String blockquote(String beginning, String text) {
    String blockquote = blockquote(beginning);
    return blockquote + text.trim().replace("\n", "\n" + blockquote) + '\n';
  }

  protected String type(Schema<?> schema) {
    String result = "object";
    if (schema instanceof ArraySchema) {
      result = "array";
    } else if (schema.getType() != null) {
      result = schema.getType();
    }
    return result;
  }

  protected String indent(int deepness) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < deepness; i++) {
      sb.append(PRE_LI);
    }
    return sb.toString();
  }

  protected Schema<?> resolve(Schema<?> schema) {
    return refPointer.resolveRef(
        diff.getNewSpecOpenApi().getComponents(), schema, schema.get$ref());
  }

  /**
   * A parameter which indicates whether or not metadata (summary and metadata) changes should be
   * logged in the changelog file.
   */
  public boolean isShowChangedMetadata() {
    return this.showChangedMetadata;
  }

  /**
   * A parameter which indicates whether or not metadata (summary and metadata) changes should be
   * logged in the changelog file.
   */
  public void setShowChangedMetadata(final boolean showChangedMetadata) {
    this.showChangedMetadata = showChangedMetadata;
  }
}
