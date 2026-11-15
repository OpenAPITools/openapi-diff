package org.openapitools.openapidiff.maven;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_PARAMS_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_PARAMS_REQUIRED_INCREASED;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.reporting.AbstractMavenReportRenderer;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedApiResponse;
import org.openapitools.openapidiff.core.model.ChangedContent;
import org.openapitools.openapidiff.core.model.ChangedHeaders;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;
import org.openapitools.openapidiff.core.model.ChangedParameter;
import org.openapitools.openapidiff.core.model.ChangedParameters;
import org.openapitools.openapidiff.core.model.ChangedRequestBody;
import org.openapitools.openapidiff.core.model.ChangedSchema;
import org.openapitools.openapidiff.core.model.ChangedSecurityRequirements;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.DiffResult;
import org.openapitools.openapidiff.core.model.Endpoint;
import org.openapitools.openapidiff.core.model.schema.ChangedEnum;
import org.openapitools.openapidiff.core.model.schema.ChangedOperationId;
import org.openapitools.openapidiff.core.model.schema.ChangedRequired;
import org.openapitools.openapidiff.core.output.HttpStatus;
import org.openapitools.openapidiff.core.utils.EndpointUtils;

/**
 * Writes a {@link ChangedOpenApi} as a Maven site page using the Doxia {@link Sink} API.
 *
 * <p>The page follows the structure of the other renderers (What's New, What's Deleted, What's
 * Deprecated, What's Changed) and starts with a summary of the overall result. Everything is
 * written as text, so the sink escapes summaries and names coming from the specifications.
 */
class OpenApiDiffReportRenderer extends AbstractMavenReportRenderer {

  static final String TITLE = "OpenAPI Diff";

  static final String BREAKS_COMPATIBILITY = " (breaks backward compatibility)";

  /** Guards the listing of schema incompatibilities against unexpectedly deep schemas. */
  private static final int MAX_SCHEMA_DEPTH = 32;

  private final ChangedOpenApi diff;
  private final String oldSpecLocation;
  private final String newSpecLocation;

  OpenApiDiffReportRenderer(
      Sink sink, ChangedOpenApi diff, String oldSpecLocation, String newSpecLocation) {
    super(sink);
    this.diff = diff;
    this.oldSpecLocation = oldSpecLocation;
    this.newSpecLocation = newSpecLocation;
  }

  @Override
  public String getTitle() {
    return TITLE;
  }

  @Override
  protected void renderBody() {
    startSection(getTitle());
    paragraph(
        "Differences between two versions of the OpenAPI specification, and whether clients"
            + " written for the old version keep working with the new one.");
    renderSummary();
    renderEndpoints("What's New", diff.getNewEndpoints());
    renderEndpoints("What's Deleted", diff.getMissingEndpoints());
    renderEndpoints("What's Deprecated", deprecatedEndpoints(diff));
    renderChangedOperations(diff.getChangedOperations());
    endSection();
  }

  /**
   * Returns the endpoints that the new specification deprecates. {@link
   * ChangedOpenApi#getDeprecatedEndpoints()} lists every change of the deprecated flag, including
   * operations that are no longer deprecated, so those are left out here.
   */
  static List<Endpoint> deprecatedEndpoints(ChangedOpenApi diff) {
    List<Endpoint> endpoints = new ArrayList<>();
    for (ChangedOperation operation : nullToEmpty(diff.getChangedOperations())) {
      if (operation.isDeprecated() && isDeprecated(operation.getNewOperation())) {
        endpoints.add(
            EndpointUtils.convert2Endpoint(
                operation.getPathUrl(), operation.getHttpMethod(), operation.getNewOperation()));
      }
    }
    return endpoints;
  }

  /** Same wording as the console and Markdown renderers. */
  static String verdict(ChangedOpenApi diff) {
    if (diff.isUnchanged()) {
      return "No differences. Specifications are equivalent";
    }
    return diff.isCompatible()
        ? "API changes are backward compatible"
        : "API changes broke backward compatibility";
  }

  static String describe(DiffResult result) {
    if (result.isUnchanged()) {
      return "No changes";
    }
    if (result.isMetaChanged()) {
      return "Metadata changes only";
    }
    return result.isCompatible() ? "Backward compatible" : "Breaks backward compatibility";
  }

  private void renderSummary() {
    startTable();
    summaryRow("Old specification", describe(diff.getOldSpecOpenApi(), oldSpecLocation), false);
    summaryRow("New specification", describe(diff.getNewSpecOpenApi(), newSpecLocation), false);
    summaryRow("Result", verdict(diff), true);
    summaryRow("New endpoints", String.valueOf(size(diff.getNewEndpoints())), false);
    summaryRow("Deleted endpoints", String.valueOf(size(diff.getMissingEndpoints())), false);
    summaryRow("Deprecated endpoints", String.valueOf(size(deprecatedEndpoints(diff))), false);
    summaryRow("Changed endpoints", String.valueOf(size(diff.getChangedOperations())), false);
    endTable();
  }

  private void summaryRow(String name, String value, boolean strong) {
    sink.tableRow();
    sink.tableHeaderCell();
    sink.text(name);
    sink.tableHeaderCell_();
    sink.tableCell();
    if (strong) {
      sink.bold();
    }
    sink.text(value);
    if (strong) {
      sink.bold_();
    }
    sink.tableCell_();
    sink.tableRow_();
  }

  private void renderEndpoints(String title, List<Endpoint> endpoints) {
    if (endpoints == null || endpoints.isEmpty()) {
      return;
    }
    startSection(title);
    startTable();
    tableHeader(new String[] {"Method", "Path", "Summary"});
    for (Endpoint endpoint : endpoints) {
      sink.tableRow();
      textCell(String.valueOf(endpoint.getMethod()));
      codeCell(endpoint.getPathUrl());
      textCell(endpoint.getSummary());
      sink.tableRow_();
    }
    endTable();
    endSection();
  }

  private void renderChangedOperations(List<ChangedOperation> operations) {
    if (operations == null || operations.isEmpty()) {
      return;
    }
    startSection("What's Changed");
    for (ChangedOperation operation : operations) {
      startSection(operation.getHttpMethod() + " " + operation.getPathUrl());
      String summary = summaryOf(operation);
      if (summary != null && !summary.trim().isEmpty()) {
        paragraph(summary);
      }
      sink.paragraph();
      sink.bold();
      sink.text("Compatibility:");
      sink.bold_();
      sink.text(" " + describe(operation.isChanged()));
      sink.paragraph_();
      renderLines(describeChanges(operation));
      endSection();
    }
    endSection();
  }

  private void renderLines(List<Line> lines) {
    if (lines.isEmpty()) {
      return;
    }
    sink.list();
    for (Line line : lines) {
      sink.listItem();
      sink.text(line.text);
      renderLines(line.children);
      sink.listItem_();
    }
    sink.list_();
  }

  /** Lists what changed in an operation, grouped like the console renderer. */
  List<Line> describeChanges(ChangedOperation operation) {
    List<Line> lines = new ArrayList<>();
    if (operation.isDeprecated()) {
      boolean wasDeprecated = isDeprecated(operation.getOldOperation());
      boolean isDeprecated = isDeprecated(operation.getNewOperation());
      if (isDeprecated && !wasDeprecated) {
        lines.add(new Line("Operation deprecated"));
      } else if (wasDeprecated && !isDeprecated) {
        lines.add(new Line("Operation no longer deprecated"));
      }
    }
    ChangedOperationId operationId = operation.getOperationId();
    if (operationId != null && operationId.isDifferent()) {
      lines.add(
          new Line(
              "Operation ID changed from "
                  + valueOrNone(operationId.getLeft())
                  + " to "
                  + valueOrNone(operationId.getRight())));
    }
    addGroup(lines, parameters(operation.getParameters()));
    addGroup(lines, requestBody(operation.getRequestBody()));
    addGroup(lines, responses(operation.getApiResponses()));
    addGroup(lines, security(operation.getSecurityRequirements()));
    return lines;
  }

  private static void addGroup(List<Line> lines, Line group) {
    if (!group.children.isEmpty()) {
      lines.add(group);
    }
  }

  private static Line parameters(ChangedParameters parameters) {
    Line group = new Line("Parameters");
    if (parameters == null) {
      return group;
    }
    // same rules as ChangedParameters#isCoreChanged
    DiffContext context = parameters.getContext();
    boolean requiredIncreasedBreaks = REQUEST_PARAMS_REQUIRED_INCREASED.enabled(context);
    for (Parameter parameter : nullToEmpty(parameters.getIncreased())) {
      boolean breaks = requiredIncreasedBreaks && Boolean.TRUE.equals(parameter.getRequired());
      group.add("Added " + describe(parameter) + marker(breaks));
    }
    for (ChangedParameter parameter : nullToEmpty(parameters.getChanged())) {
      Parameter newParameter = parameter.getNewParameter();
      boolean deprecated =
          parameter.isDeprecated() && Boolean.TRUE.equals(newParameter.getDeprecated());
      String text = (deprecated ? "Deprecated " : "Changed ") + describe(newParameter);
      group.add(parameter.isIncompatible() ? text + BREAKS_COMPATIBILITY : text);
    }
    boolean decreasedBreaks = REQUEST_PARAMS_DECREASED.enabled(context);
    for (Parameter parameter : nullToEmpty(parameters.getMissing())) {
      group.add("Deleted " + describe(parameter) + marker(decreasedBreaks));
    }
    return group;
  }

  private Line requestBody(ChangedRequestBody requestBody) {
    Line group = new Line("Request body");
    if (requestBody == null) {
      return group;
    }
    RequestBody newRequestBody = requestBody.getNewRequestBody();
    if (requestBody.isChangeRequired() && newRequestBody != null) {
      boolean required = Boolean.TRUE.equals(newRequestBody.getRequired());
      String text = required ? "Request body is now required" : "Request body is now optional";
      // only the required flag is evaluated by the core result, not the content
      group.add(requestBody.isCoreChanged().isIncompatible() ? text + BREAKS_COMPATIBILITY : text);
    }
    content(group, requestBody.getContent());
    return group;
  }

  private Line responses(ChangedApiResponse responses) {
    Line group = new Line("Responses");
    if (responses == null) {
      return group;
    }
    for (String code : nullToEmpty(responses.getIncreased()).keySet()) {
      group.add("Added " + describeStatus(code));
    }
    // deleted responses are the only reason for an incompatible core result
    boolean deletionsBreak = responses.isCoreChanged().isIncompatible();
    for (String code : nullToEmpty(responses.getMissing()).keySet()) {
      group.add("Deleted " + describeStatus(code) + marker(deletionsBreak));
    }
    nullToEmpty(responses.getChanged())
        .forEach(
            (code, response) -> {
              Line line = group.add("Changed " + describeStatus(code));
              if (response != null) {
                content(line, response.getContent());
                headers(line, response.getHeaders());
              }
            });
    return group;
  }

  private void content(Line group, ChangedContent content) {
    if (content == null) {
      return;
    }
    for (String mediaType : nullToEmpty(content.getIncreased()).keySet()) {
      group.add("Added " + mediaType);
    }
    // deleted media types are the only reason for an incompatible core result
    boolean deletionsBreak = content.isCoreChanged().isIncompatible();
    for (String mediaType : nullToEmpty(content.getMissing()).keySet()) {
      group.add("Deleted " + mediaType + marker(deletionsBreak));
    }
    nullToEmpty(content.getChanged())
        .forEach(
            (mediaType, changedMediaType) -> {
              if (!changedMediaType.isCompatible()) {
                Line line = group.add("Changed " + mediaType + ": schema breaks compatibility");
                incompatibilities(line, "", changedMediaType.getSchema(), 0);
              } else if (changedMediaType.getSchema() != null) {
                group.add("Changed " + mediaType + ": schema is backward compatible");
              } else {
                group.add("Changed " + mediaType + ": examples only");
              }
            });
  }

  private static void headers(Line group, ChangedHeaders headers) {
    if (headers == null) {
      return;
    }
    for (String name : nullToEmpty(headers.getIncreased()).keySet()) {
      group.add("Added header " + name);
    }
    // deleted headers are the only reason for an incompatible core result
    boolean deletionsBreak = headers.isCoreChanged().isIncompatible();
    for (String name : nullToEmpty(headers.getMissing()).keySet()) {
      group.add("Deleted header " + name + marker(deletionsBreak));
    }
    nullToEmpty(headers.getChanged())
        .forEach(
            (name, header) ->
                group.add(
                    "Changed header "
                        + name
                        + (header.isIncompatible() ? BREAKS_COMPATIBILITY : "")));
  }

  private static Line security(ChangedSecurityRequirements security) {
    Line group = new Line("Security requirements");
    if (security == null) {
      return group;
    }
    for (SecurityRequirement requirement : nullToEmpty(security.getIncreased())) {
      group.add("Added " + describe(requirement));
    }
    // deleted requirements are the only reason for an incompatible core result
    boolean deletionsBreak = security.isCoreChanged().isIncompatible();
    for (SecurityRequirement requirement : nullToEmpty(security.getMissing())) {
      group.add("Deleted " + describe(requirement) + marker(deletionsBreak));
    }
    nullToEmpty(security.getChanged())
        .forEach(
            requirement ->
                group.add(
                    "Changed "
                        + describe(requirement.getOldSecurityRequirement())
                        + (requirement.isIncompatible() ? BREAKS_COMPATIBILITY : "")));
    return group;
  }

  /** Explains why a schema breaks compatibility, like the console renderer does. */
  private static void incompatibilities(Line parent, String path, ChangedSchema schema, int depth) {
    if (schema == null || depth > MAX_SCHEMA_DEPTH) {
      return;
    }
    String name = path.isEmpty() ? "schema" : path;
    if (schema.isCoreChanged() == DiffResult.INCOMPATIBLE && schema.isChangedType()) {
      parent.add(
          "Changed type of "
              + name
              + ": "
              + type(schema.getOldSchema())
              + " -> "
              + type(schema.getNewSchema()));
    }
    if (schema.isCoreChanged() == DiffResult.INCOMPATIBLE
        && schema.isDiscriminatorPropertyChanged()) {
      parent.add("Changed discriminator of " + name);
    }
    // constraints that can break compatibility on their own
    changedIfIncompatible(parent, schema.getMaxLength(), "maxLength", name);
    changedIfIncompatible(parent, schema.getMinLength(), "minLength", name);
    changedIfIncompatible(parent, schema.getNumericRange(), "numeric range", name);
    changedIfIncompatible(parent, schema.getMultipleOf(), "multipleOf", name);
    changedIfIncompatible(parent, schema.getPattern(), "pattern", name);
    changedIfIncompatible(parent, schema.getMaxItems(), "maxItems", name);
    changedIfIncompatible(parent, schema.getMinItems(), "minItems", name);
    changedIfIncompatible(parent, schema.getUniqueItems(), "uniqueItems", name);
    changedIfIncompatible(parent, schema.getMaxProperties(), "maxProperties", name);
    changedIfIncompatible(parent, schema.getMinProperties(), "minProperties", name);
    changedIfIncompatible(parent, schema.getReadOnly(), "readOnly", name);
    changedIfIncompatible(parent, schema.getWriteOnly(), "writeOnly", name);
    changedIfIncompatible(parent, schema.getOneOfSchema(), "oneOf alternatives", name);
    changedIfIncompatible(parent, schema.getAddProp(), "additionalProperties", name);
    ChangedRequired required = schema.getRequired();
    if (required != null && required.isIncompatible()) {
      for (String property : required.getIncreased()) {
        parent.add("New required property " + join(path, property));
      }
      for (String property : required.getMissing()) {
        parent.add("Property " + join(path, property) + " is no longer required");
      }
    }
    ChangedEnum<?> enumeration = schema.getEnumeration();
    if (enumeration != null && enumeration.isIncompatible()) {
      if (!enumeration.getMissing().isEmpty()) {
        parent.add("Removed values of " + name + ": " + enumeration.getMissing());
      }
      if (!enumeration.getIncreased().isEmpty()) {
        parent.add("Added values of " + name + ": " + enumeration.getIncreased());
      }
    }
    schema
        .getMissingProperties()
        .forEach(
            (property, propertySchema) ->
                parent.add(
                    "Removed property "
                        + join(path, property)
                        + " ("
                        + type(propertySchema)
                        + ")"));
    if (schema.getItems() != null) {
      incompatibilities(parent, path + "[n]", schema.getItems(), depth + 1);
    }
    schema
        .getChangedProperties()
        .forEach(
            (property, changed) ->
                incompatibilities(parent, join(path, property), changed, depth + 1));
  }

  private static String marker(boolean breaksCompatibility) {
    return breaksCompatibility ? BREAKS_COMPATIBILITY : "";
  }

  private static boolean isDeprecated(Operation operation) {
    return operation != null && Boolean.TRUE.equals(operation.getDeprecated());
  }

  private static void changedIfIncompatible(
      Line parent, Changed changed, String constraint, String name) {
    if (changed != null && changed.isIncompatible()) {
      parent.add("Changed " + constraint + " of " + name);
    }
  }

  private void textCell(String text) {
    sink.tableCell();
    if (text != null) {
      sink.text(text);
    }
    sink.tableCell_();
  }

  private void codeCell(String text) {
    sink.tableCell();
    sink.inline(SinkEventAttributeSet.Semantics.CODE);
    sink.text(text);
    sink.inline_();
    sink.tableCell_();
  }

  private static String describe(OpenAPI spec, String location) {
    Info info = spec != null ? spec.getInfo() : null;
    StringBuilder description = new StringBuilder();
    if (info != null && info.getTitle() != null) {
      description.append(info.getTitle());
    }
    if (info != null && info.getVersion() != null) {
      description.append(description.length() > 0 ? " " : "").append(info.getVersion());
    }
    if (location != null) {
      description.append(description.length() > 0 ? " (" + location + ")" : location);
    }
    return description.toString();
  }

  private static String describe(Parameter parameter) {
    String name = parameter.getName() != null ? parameter.getName() : parameter.get$ref();
    return name + " in " + parameter.getIn();
  }

  private static String describe(SecurityRequirement requirement) {
    if (requirement == null || requirement.isEmpty()) {
      return "anonymous access";
    }
    return "requirement " + String.join(", ", requirement.keySet());
  }

  static String describeStatus(String code) {
    try {
      String reason = HttpStatus.getReasonPhrase(Integer.parseInt(code));
      return reason != null ? code + " " + reason : code;
    } catch (NumberFormatException e) {
      // "default" and ranges such as "2XX" have no reason phrase
      return code;
    }
  }

  private static String summaryOf(ChangedOperation operation) {
    Operation newOperation = operation.getNewOperation();
    Operation operationWithSummary =
        newOperation != null ? newOperation : operation.getOldOperation();
    return operationWithSummary != null ? operationWithSummary.getSummary() : null;
  }

  private static String type(Schema<?> schema) {
    if (schema == null) {
      return "no schema";
    }
    if (schema.get$ref() != null) {
      return schema.get$ref().substring(schema.get$ref().lastIndexOf('/') + 1);
    }
    String type = schema instanceof ArraySchema ? "array" : schema.getType();
    if (type == null) {
      type = "object";
    }
    return schema.getFormat() != null ? type + " (" + schema.getFormat() + ")" : type;
  }

  private static String join(String path, String property) {
    return path.isEmpty() ? property : path + "." + property;
  }

  private static String valueOrNone(String value) {
    return value != null ? value : "none";
  }

  private static int size(Collection<?> values) {
    return values != null ? values.size() : 0;
  }

  private static <T> List<T> nullToEmpty(List<T> values) {
    return values != null ? values : Collections.emptyList();
  }

  private static <K, V> Map<K, V> nullToEmpty(Map<K, V> values) {
    return values != null ? values : Collections.emptyMap();
  }

  /** One entry of the nested list of changes of an operation. */
  static final class Line {
    final String text;
    final List<Line> children = new ArrayList<>();

    Line(String text) {
      this.text = text;
    }

    Line add(String childText) {
      Line child = new Line(childText);
      children.add(child);
      return child;
    }
  }
}
