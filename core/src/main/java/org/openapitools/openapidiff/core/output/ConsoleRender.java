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
import org.openapitools.openapidiff.core.model.schema.ChangedOperationId;
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
      safelyAppend(outputStreamWriter, I18n.getMessage("no.differences"));
    } else {
      safelyAppend(outputStreamWriter, bigTitle(I18n.getMessage("api.change.log")));
      safelyAppend(
          outputStreamWriter,
          centerCjk(diff.getNewSpecOpenApi().getInfo().getTitle(), LINE_LENGTH));
      safelyAppend(outputStreamWriter, System.lineSeparator());

      List<Endpoint> newEndpoints = diff.getNewEndpoints();
      listEndpoints(newEndpoints, I18n.getMessage("whats.new"), outputStreamWriter);

      List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
      listEndpoints(missingEndpoints, I18n.getMessage("whats.deleted"), outputStreamWriter);

      List<Endpoint> deprecatedEndpoints = diff.getDeprecatedEndpoints();
      listEndpoints(deprecatedEndpoints, I18n.getMessage("whats.deprecated"), outputStreamWriter);

      List<ChangedOperation> changedOperations = diff.getChangedOperations();
      ol_changed(changedOperations, outputStreamWriter);

      safelyAppend(outputStreamWriter, title(I18n.getMessage("result")));
      safelyAppend(
          outputStreamWriter,
          centerCjk(
              diff.isCompatible()
                  ? I18n.getMessage("api.changes.backward.compatible")
                  : I18n.getMessage("api.changes.broke.compatibility"),
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
    safelyAppend(outputStreamWriter, title(I18n.getMessage("whats.changed")));
    for (ChangedOperation operation : operations) {
      String pathUrl = operation.getPathUrl();
      String method = operation.getHttpMethod().toString();
      String desc =
          Optional.ofNullable(operation.getSummary()).map(ChangedMetadata::getRight).orElse("");

      safelyAppend(outputStreamWriter, itemEndpoint(method, pathUrl, desc));

      if (result(operation.getOperationId()).isDifferent()) {
        safelyAppend(outputStreamWriter, StringUtils.repeat(' ', 2));
        safelyAppend(outputStreamWriter, I18n.getMessage("operation.id") + ":");
        safelyAppend(outputStreamWriter, System.lineSeparator());
        safelyAppend(outputStreamWriter, ul_operation_id(operation.getOperationId()));
      }
      if (result(operation.getParameters()).isDifferent()) {
        safelyAppend(outputStreamWriter, StringUtils.repeat(' ', 2));
        safelyAppend(outputStreamWriter, I18n.getMessage("parameter") + ":");
        safelyAppend(outputStreamWriter, System.lineSeparator());
        safelyAppend(outputStreamWriter, ul_param(operation.getParameters()));
      }
      if (operation.resultRequestBody().isDifferent()) {
        safelyAppend(outputStreamWriter, StringUtils.repeat(' ', 2));
        safelyAppend(outputStreamWriter, I18n.getMessage("request") + ":");
        safelyAppend(outputStreamWriter, System.lineSeparator());
        safelyAppend(outputStreamWriter, ul_content(operation.getRequestBody().getContent(), true));
      }
      if (operation.resultApiResponses().isDifferent()) {
        safelyAppend(outputStreamWriter, StringUtils.repeat(' ', 2));
        safelyAppend(outputStreamWriter, I18n.getMessage("return.type") + ":");
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
      sb.append(itemResponse(I18n.getMessage("action.add") + " ", propName));
    }
    for (String propName : delResponses.keySet()) {
      sb.append(itemResponse(I18n.getMessage("action.deleted") + " ", propName));
    }
    for (Entry<String, ChangedResponse> entry : changedResponses.entrySet()) {
      sb.append(
          itemChangedResponse(
              I18n.getMessage("action.changed") + " ", entry.getKey(), entry.getValue()));
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
        + I18n.getMessage("media.types")
        + ":"
        + System.lineSeparator()
        + ul_content(response.getContent(), false);
  }

  private String ul_content(ChangedContent changedContent, boolean isRequest) {
    StringBuilder sb = new StringBuilder();
    if (changedContent == null) {
      return sb.toString();
    }
    for (String propName : changedContent.getIncreased().keySet()) {
      sb.append(itemContent(I18n.getMessage("action.added") + " ", propName));
    }
    for (String propName : changedContent.getMissing().keySet()) {
      sb.append(itemContent(I18n.getMessage("action.deleted") + " ", propName));
    }
    for (String propName : changedContent.getChanged().keySet()) {
      sb.append(
          itemContent(
              I18n.getMessage("action.changed") + " ",
              propName,
              changedContent.getChanged().get(propName),
              isRequest));
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
        .append(I18n.getMessage("schema") + ": ")
        .append(
            changedMediaType.isCompatible()
                ? I18n.getMessage("backward.compatible")
                : I18n.getMessage("broken.compatibility"))
        .append(System.lineSeparator());
    if (!changedMediaType.isCompatible() && changedMediaType.getSchema() != null) {
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
      sb.append(property(propName, I18n.getMessage("changed.property.type"), type));
    }
    String prefix = propName.isEmpty() ? "" : propName + ".";
    sb.append(
        properties(
            prefix,
            I18n.getMessage("missing.property"),
            schema.getMissingProperties(),
            schema.getContext()));
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
      sb.append(itemParam(I18n.getMessage("action.add") + " ", param));
    }
    for (ChangedParameter param : changed) {
      sb.append(li_changedParam(param));
    }
    for (Parameter param : delParameters) {
      sb.append(itemParam(I18n.getMessage("action.delete") + " ", param));
    }
    return sb.toString();
  }

  private String itemParam(String title, Parameter param) {
    return ""
        + StringUtils.repeat(' ', 4)
        + "- "
        + title
        + param.getName()
        + " "
        + I18n.getMessage("in")
        + " "
        + param.getIn()
        + System.lineSeparator();
  }

  private String li_changedParam(ChangedParameter changeParam) {
    if (changeParam.isDeprecated()) {
      return itemParam(I18n.getMessage("action.deprecated") + " ", changeParam.getNewParameter());
    } else {
      return itemParam(I18n.getMessage("action.changed") + " ", changeParam.getNewParameter());
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

  private String ul_operation_id(ChangedOperationId operationId) {
    return String.format(
        "    - %s %s %s %s\n",
        I18n.getMessage("action.changed"),
        operationId.getLeft(),
        I18n.getMessage("to"),
        operationId.getRight());
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
        separator(ch), little, centerCjk(title, LINE_LENGTH - 4), little, separator(ch));
  }

  public String separator(char ch) {
    return StringUtils.repeat(ch, LINE_LENGTH) + System.lineSeparator();
  }

  static int displayWidth(String text) {
    if (text == null) {
      return 0;
    }
    int width = 0;
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (isFullWidth(c)) {
        width += 2;
      } else {
        width += 1;
      }
    }
    return width;
  }

  static boolean isFullWidth(char c) {
    return (c >= '\u1100' && c <= '\u115F')
        || (c >= '\u2E80' && c <= '\u303E')
        || (c >= '\u3040' && c <= '\u33BF')
        || (c >= '\u3400' && c <= '\u4DBF')
        || (c >= '\u4E00' && c <= '\u9FFF')
        || (c >= '\uA960' && c <= '\uA97F')
        || (c >= '\uAC00' && c <= '\uD7FF')
        || (c >= '\uF900' && c <= '\uFAFF')
        || (c >= '\uFE30' && c <= '\uFE4F')
        || (c >= '\uFF00' && c <= '\uFF60')
        || (c >= '\uFFE0' && c <= '\uFFE6');
  }

  static String centerCjk(String text, int width) {
    if (text == null) {
      text = "";
    }
    int textWidth = displayWidth(text);
    if (textWidth >= width) {
      return text;
    }
    int totalPadding = width - textWidth;
    int leftPadding = totalPadding / 2;
    int rightPadding = totalPadding - leftPadding;
    return StringUtils.repeat(' ', leftPadding) + text + StringUtils.repeat(' ', rightPadding);
  }
}
