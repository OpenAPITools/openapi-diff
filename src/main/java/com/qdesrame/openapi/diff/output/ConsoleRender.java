package com.qdesrame.openapi.diff.output;

import static com.qdesrame.openapi.diff.model.Changed.result;

import com.qdesrame.openapi.diff.model.*;
import com.qdesrame.openapi.diff.utils.RefPointer;
import com.qdesrame.openapi.diff.utils.RefType;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;

public class ConsoleRender implements Render {
  private static final int LINE_LENGTH = 74;
  protected static RefPointer<Schema> refPointer = new RefPointer<>(RefType.SCHEMAS);
  protected ChangedOpenApi diff;

  private StringBuilder output;

  @Override
  public String render(ChangedOpenApi diff) {
    this.diff = diff;
    output = new StringBuilder();
    if (diff.isUnchanged()) {
      output.append("No differences. Specifications are equivalents");
    } else {
      output
          .append(bigTitle("Api Change Log"))
          .append(StringUtils.center(diff.getNewSpecOpenApi().getInfo().getTitle(), LINE_LENGTH))
          .append(System.lineSeparator());

      List<Endpoint> newEndpoints = diff.getNewEndpoints();
      String ol_newEndpoint = listEndpoints(newEndpoints, "What's New");

      List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
      String ol_missingEndpoint = listEndpoints(missingEndpoints, "What's Deleted");

      List<Endpoint> deprecatedEndpoints = diff.getDeprecatedEndpoints();
      String ol_deprecatedEndpoint = listEndpoints(deprecatedEndpoints, "What's Deprecated");

      List<ChangedOperation> changedOperations = diff.getChangedOperations();
      String ol_changed = ol_changed(changedOperations);

      output
          .append(renderBody(ol_newEndpoint, ol_missingEndpoint, ol_deprecatedEndpoint, ol_changed))
          .append(title("Result"))
          .append(
              StringUtils.center(
                  diff.isCompatible()
                      ? "API changes are backward compatible"
                      : "API changes broke backward compatibility",
                  LINE_LENGTH))
          .append(System.lineSeparator())
          .append(separator('-'));
    }
    return output.toString();
  }

  private String ol_changed(List<ChangedOperation> operations) {
    if (null == operations || operations.size() == 0) return "";
    StringBuilder sb = new StringBuilder();
    sb.append(title("What's Changed"));
    for (ChangedOperation operation : operations) {
      String pathUrl = operation.getPathUrl();
      String method = operation.getHttpMethod().toString();
      String desc =
          Optional.ofNullable(operation.getSummary()).map(ChangedMetadata::getRight).orElse("");

      StringBuilder ul_detail = new StringBuilder();
      if (result(operation.getParameters()).isDifferent()) {
        ul_detail
            .append(StringUtils.repeat(' ', 2))
            .append("Parameter:")
            .append(System.lineSeparator())
            .append(ul_param(operation.getParameters()));
      }
      if (operation.resultRequestBody().isDifferent()) {
        ul_detail
            .append(StringUtils.repeat(' ', 2))
            .append("Request:")
            .append(System.lineSeparator())
            .append(ul_content(operation.getRequestBody().getContent(), true));
      }
      if (operation.resultApiResponses().isDifferent()) {
        ul_detail
            .append(StringUtils.repeat(' ', 2))
            .append("Return Type:")
            .append(System.lineSeparator())
            .append(ul_response(operation.getApiResponses()));
      }
      sb.append(itemEndpoint(method, pathUrl, desc)).append(ul_detail);
    }
    return sb.toString();
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
    for (String propName : changedResponses.keySet()) {
      sb.append(itemChangedResponse("Changed ", propName, changedResponses.get(propName)));
    }
    return sb.toString();
  }

  private String itemResponse(String title, String code) {
    StringBuilder sb = new StringBuilder();
    String status = "";
    if (!code.equals("default")) {
      status = HttpStatus.getStatusText(Integer.parseInt(code));
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
    StringBuilder sb = new StringBuilder();
    sb.append(itemResponse(title, contentType));
    sb.append(StringUtils.repeat(' ', 6)).append("Media types:").append(System.lineSeparator());
    sb.append(ul_content(response.getContent(), false));
    return sb.toString();
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
    StringBuilder sb = new StringBuilder();
    sb.append(StringUtils.repeat(' ', 8))
        .append("- ")
        .append(title)
        .append(contentType)
        .append(System.lineSeparator());
    return sb.toString();
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
    StringBuilder sb = new StringBuilder();
    sb.append(incompatibilities(propName + "[n]", schema));
    return sb.toString();
  }

  private String properties(
      String propPrefix, String title, Map<String, Schema> properties, DiffContext context) {
    StringBuilder sb = new StringBuilder();
    if (properties != null) {
      properties.forEach(
          (key, value) -> sb.append(property(propPrefix + key, title, resolve(value))));
    }
    return sb.toString();
  }

  protected String property(String name, String title, Schema schema) {
    return property(name, title, type(schema));
  }

  protected String property(String name, String title, String type) {
    return String.format("%s%s: %s (%s)\n", StringUtils.repeat(' ', 10), title, name, type);
  }

  protected Schema resolve(Schema schema) {
    return refPointer.resolveRef(
        diff.getNewSpecOpenApi().getComponents(), schema, schema.get$ref());
  }

  protected String type(Schema schema) {
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
    StringBuilder sb = new StringBuilder("");
    sb.append(StringUtils.repeat(' ', 4))
        .append("- ")
        .append(title)
        .append(param.getName())
        .append(" in ")
        .append(param.getIn())
        .append(System.lineSeparator());
    //                .append(null == param.getDescription() ? ""
    //                        : (" //" + param.getDescription()));
    return sb.toString();
  }

  private String li_changedParam(ChangedParameter changeParam) {
    if (changeParam.isDeprecated()) {
      return itemParam("Deprecated ", changeParam.getNewParameter());
    } else {
      return itemParam("Changed ", changeParam.getNewParameter());
    }
  }

  private String listEndpoints(List<Endpoint> endpoints, String title) {
    if (null == endpoints || endpoints.size() == 0) return "";
    StringBuilder sb = new StringBuilder();
    sb.append(title(title));
    for (Endpoint endpoint : endpoints) {
      sb.append(
          itemEndpoint(
              endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
    }
    return sb.append(System.lineSeparator()).toString();
  }

  private String itemEndpoint(String method, String path, String desc) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("- %s %s%n", StringUtils.rightPad(method, 6), path));
    return sb.toString();
  }

  public String renderBody(String ol_new, String ol_miss, String ol_deprec, String ol_changed) {
    StringBuilder sb = new StringBuilder();
    sb.append(ol_new).append(ol_miss).append(ol_deprec).append(ol_changed);
    return sb.toString();
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

  public StringBuilder separator(char ch) {
    StringBuilder sb = new StringBuilder();
    return sb.append(StringUtils.repeat(ch, LINE_LENGTH)).append(System.lineSeparator());
  }
}
