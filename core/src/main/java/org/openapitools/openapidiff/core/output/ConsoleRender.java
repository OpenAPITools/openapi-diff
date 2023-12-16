package org.openapitools.openapidiff.core.output;

import static org.openapitools.openapidiff.core.model.Changed.result;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.openapidiff.core.exception.RendererException;
import org.openapitools.openapidiff.core.model.*;
import org.openapitools.openapidiff.core.utils.RefPointer;
import org.openapitools.openapidiff.core.utils.RefType;

public class ConsoleRender implements Render {
  private static final int LINE_LENGTH = 74;
  protected static RefPointer<Schema<?>> refPointer = new RefPointer<>(RefType.SCHEMAS);
  protected ChangedOpenApi diff;

  @Override
  public void render(ChangedOpenApi diff, OutputStreamWriter outputStreamWriter) {
    this.diff = diff;
    if (diff.isUnchanged()) {
      safelyAppend(outputStreamWriter, "No differences. Specifications are equivalents");
    } else {
      safelyAppend(outputStreamWriter, bigTitle("Api Change Log"));
      safelyAppend(
          outputStreamWriter,
          StringUtils.center(diff.getNewSpecOpenApi().getInfo().getTitle(), LINE_LENGTH));
      safelyAppend(outputStreamWriter, System.lineSeparator());

      List<Endpoint> newEndpoints = diff.getNewEndpoints();
      listEndpoints(newEndpoints, "What's New", outputStreamWriter);

      List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
      listEndpoints(missingEndpoints, "What's Deleted", outputStreamWriter);

      List<Endpoint> deprecatedEndpoints = diff.getDeprecatedEndpoints();
      listEndpoints(deprecatedEndpoints, "What's Deprecated", outputStreamWriter);

      List<ChangedOperation> changedOperations = diff.getChangedOperations();
      ol_changed(changedOperations, outputStreamWriter);

      safelyAppend(outputStreamWriter, title("Result"));
      safelyAppend(
          outputStreamWriter,
          StringUtils.center(
              diff.isCompatible()
                  ? "API changes are backward compatible"
                  : "API changes broke backward compatibility",
              LINE_LENGTH));
      safelyAppend(outputStreamWriter, System.lineSeparator());
      safelyAppend(outputStreamWriter, separator('-'));
    }
    try {
      outputStreamWriter.close();
    } catch (IOException e) {
      throw new RendererException(e);
    }
  }

  private void ol_changed(
      List<ChangedOperation> operations, OutputStreamWriter outputStreamWriter) {
    if (null == operations || operations.isEmpty()) {
      return;
    }
    safelyAppend(outputStreamWriter, title("What's Changed"));
    for (ChangedOperation operation : operations) {
      String pathUrl = operation.getPathUrl();
      String method = operation.getHttpMethod().toString();
      String desc =
          Optional.ofNullable(operation.getSummary()).map(ChangedMetadata::getRight).orElse("");

      safelyAppend(outputStreamWriter, itemEndpoint(method, pathUrl, desc));

      if (result(operation.getParameters()).isDifferent()) {
        safelyAppend(outputStreamWriter, StringUtils.repeat(' ', 2));
        safelyAppend(outputStreamWriter, "Parameter:");
        safelyAppend(outputStreamWriter, System.lineSeparator());
        safelyAppend(outputStreamWriter, ul_param(operation.getParameters()));
      }
      if (operation.resultRequestBody().isDifferent()) {
        safelyAppend(outputStreamWriter, StringUtils.repeat(' ', 2));
        safelyAppend(outputStreamWriter, "Request:");
        safelyAppend(outputStreamWriter, System.lineSeparator());
        safelyAppend(outputStreamWriter, ul_content(operation.getRequestBody().getContent(), true));
      }
      if (operation.resultApiResponses().isDifferent()) {
        safelyAppend(outputStreamWriter, StringUtils.repeat(' ', 2));
        safelyAppend(outputStreamWriter, "Return Type:");
        safelyAppend(outputStreamWriter, System.lineSeparator());
        safelyAppend(outputStreamWriter, ul_response(operation.getApiResponses()));
      }
    }
  }

  private String ul_response(ChangedApiResponse changedApiResponse) {
    Map<String, ApiResponse> addResponses = changedApiResponse.getIncreased();
    Map<String, ApiResponse> delResponses = changedApiResponse.getMissing();
    Map<String, ChangedResponse> changedResponses = changedApiResponse.getChanged();
    StringBuilder sb = new StringBuilder();
    for (String propName : addResponses.keySet()) {
      sb.append(itemResponse("Add ", propName));
    }
    for (String propName : delResponses.keySet()) {
      sb.append(itemResponse("Deleted ", propName));
    }
    for (Entry<String, ChangedResponse> entry : changedResponses.entrySet()) {
      sb.append(itemChangedResponse("Changed ", entry.getKey(), entry.getValue()));
    }
    return sb.toString();
  }

  private String itemResponse(String title, String code) {
    StringBuilder sb = new StringBuilder();
    String status = "";
    if (!code.equals("default") && !code.matches("[1-5]XX")) {
      status = HttpStatus.getReasonPhrase(Integer.parseInt(code));
    }
    sb.append(StringUtils.repeat(' ', 4))
        .append("- ")
        .append(title)
        .append(code)
        .append(' ')
        .append(status)
        .append(System.lineSeparator());
    return sb.toString();
  }

  private String itemChangedResponse(String title, String contentType, ChangedResponse response) {
    return itemResponse(title, contentType)
        + StringUtils.repeat(' ', 6)
        + "Media types:"
        + System.lineSeparator()
        + ul_content(response.getContent(), false);
  }

  private String ul_content(ChangedContent changedContent, boolean isRequest) {
    StringBuilder sb = new StringBuilder();
    if (changedContent == null) {
      return sb.toString();
    }
    for (String propName : changedContent.getIncreased().keySet()) {
      sb.append(itemContent("Added ", propName));
    }
    for (String propName : changedContent.getMissing().keySet()) {
      sb.append(itemContent("Deleted ", propName));
    }
    for (String propName : changedContent.getChanged().keySet()) {
      sb.append(
          itemContent("Changed ", propName, changedContent.getChanged().get(propName), isRequest));
    }
    return sb.toString();
  }

  private String itemContent(String title, String contentType) {
    return StringUtils.repeat(' ', 8) + "- " + title + contentType + System.lineSeparator();
  }

  private String itemContent(
      String title, String contentType, ChangedMediaType changedMediaType, boolean isRequest) {
    StringBuilder sb = new StringBuilder();
    sb.append(itemContent(title, contentType))
        .append(StringUtils.repeat(' ', 10))
        .append("Schema: ")
        .append(changedMediaType.isCompatible() ? "Backward compatible" : "Broken compatibility")
        .append(System.lineSeparator());
    if (!changedMediaType.isCompatible()) {
      sb.append(incompatibilities(changedMediaType.getSchema()));
    }
    return sb.toString();
  }

  private String incompatibilities(final ChangedSchema schema) {
    return incompatibilities("", schema);
  }

  private String incompatibilities(String propName, final ChangedSchema schema) {
    StringBuilder sb = new StringBuilder();
    if (schema.getItems() != null) {
      sb.append(items(propName, schema.getItems()));
    }
    if (schema.isCoreChanged() == DiffResult.INCOMPATIBLE && schema.isChangedType()) {
      String type = type(schema.getOldSchema()) + " -> " + type(schema.getNewSchema());
      sb.append(property(propName, "Changed property type", type));
    }
    String prefix = propName.isEmpty() ? "" : propName + ".";
    sb.append(
        properties(prefix, "Missing property", schema.getMissingProperties(), schema.getContext()));
    schema
        .getChangedProperties()
        .forEach((name, property) -> sb.append(incompatibilities(prefix + name, property)));
    return sb.toString();
  }

  private String items(String propName, ChangedSchema schema) {
    return incompatibilities(propName + "[n]", schema);
  }

  private String properties(
      String propPrefix, String title, Map<String, Schema<?>> properties, DiffContext context) {
    StringBuilder sb = new StringBuilder();
    if (properties != null) {
      properties.forEach((key, value) -> sb.append(resolveProperty(propPrefix, value, key, title)));
    }
    return sb.toString();
  }

  private String resolveProperty(String propPrefix, Schema<?> value, String key, String title) {
    try {
      return property(propPrefix + key, title, resolve(value));
    } catch (Exception e) {
      return property(propPrefix + key, title, type(value));
    }
  }

  protected String property(String name, String title, Schema<?> schema) {
    return property(name, title, type(schema));
  }

  protected String property(String name, String title, String type) {
    return String.format("%s%s: %s (%s)%n", StringUtils.repeat(' ', 10), title, name, type);
  }

  protected Schema<?> resolve(Schema<?> schema) {
    return refPointer.resolveRef(
        diff.getNewSpecOpenApi().getComponents(), schema, schema.get$ref());
  }

  protected String type(Schema<?> schema) {
    String result = "object";
    if (schema == null) {
      result = "no schema";
    } else if (schema instanceof ArraySchema) {
      result = "array";
    } else if (schema.getType() != null) {
      result = schema.getType();
    }
    return result;
  }

  private String ul_param(ChangedParameters changedParameters) {
    List<Parameter> addParameters = changedParameters.getIncreased();
    List<Parameter> delParameters = changedParameters.getMissing();
    List<ChangedParameter> changed = changedParameters.getChanged();
    StringBuilder sb = new StringBuilder();
    for (Parameter param : addParameters) {
      sb.append(itemParam("Add ", param));
    }
    for (ChangedParameter param : changed) {
      sb.append(li_changedParam(param));
    }
    for (Parameter param : delParameters) {
      sb.append(itemParam("Delete ", param));
    }
    return sb.toString();
  }

  private String itemParam(String title, Parameter param) {
    return ""
        + StringUtils.repeat(' ', 4)
        + "- "
        + title
        + param.getName()
        + " in "
        + param.getIn()
        + System.lineSeparator();
  }

  private String li_changedParam(ChangedParameter changeParam) {
    if (changeParam.isDeprecated()) {
      return itemParam("Deprecated ", changeParam.getNewParameter());
    } else {
      return itemParam("Changed ", changeParam.getNewParameter());
    }
  }

  private void listEndpoints(
      List<Endpoint> endpoints, String title, OutputStreamWriter outputStreamWriter) {
    if (null == endpoints || endpoints.isEmpty()) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    sb.append(title(title));
    for (Endpoint endpoint : endpoints) {
      sb.append(
          itemEndpoint(
              endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
    }

    safelyAppend(outputStreamWriter, sb.append(System.lineSeparator()).toString());
  }

  private String itemEndpoint(String method, String path, String desc) {
    return String.format("- %s %s%n", StringUtils.rightPad(method, 6), path);
  }

  public String renderBody(String ol_new, String ol_miss, String ol_deprec, String ol_changed) {
    return ol_new + ol_miss + ol_deprec + ol_changed;
  }

  public String bigTitle(String title) {
    char ch = '=';
    return this.title(title.toUpperCase(), ch);
  }

  public String title(String title) {
    return this.title(title, '-');
  }

  public String title(String title, char ch) {
    String little = StringUtils.repeat(ch, 2);
    return String.format(
        "%s%s%s%s%n%s",
        separator(ch), little, StringUtils.center(title, LINE_LENGTH - 4), little, separator(ch));
  }

  public String separator(char ch) {
    return StringUtils.repeat(ch, LINE_LENGTH) + System.lineSeparator();
  }
}
