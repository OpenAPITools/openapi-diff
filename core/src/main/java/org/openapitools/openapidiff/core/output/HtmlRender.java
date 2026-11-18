package org.openapitools.openapidiff.core.output;

import static j2html.TagCreator.body;
import static j2html.TagCreator.del;
import static j2html.TagCreator.div;
import static j2html.TagCreator.document;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.head;
import static j2html.TagCreator.header;
import static j2html.TagCreator.hr;
import static j2html.TagCreator.html;
import static j2html.TagCreator.li;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.ol;
import static j2html.TagCreator.p;
import static j2html.TagCreator.span;
import static j2html.TagCreator.title;
import static j2html.TagCreator.ul;
import static org.openapitools.openapidiff.core.model.Changed.result;

import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import j2html.rendering.FlatHtml;
import j2html.tags.ContainerTag;
import j2html.tags.specialized.DivTag;
import j2html.tags.specialized.HtmlTag;
import j2html.tags.specialized.LiTag;
import j2html.tags.specialized.OlTag;
import j2html.tags.specialized.PTag;
import j2html.tags.specialized.UlTag;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.openapidiff.core.exception.RendererException;
import org.openapitools.openapidiff.core.model.ChangedApiResponse;
import org.openapitools.openapidiff.core.model.ChangedContent;
import org.openapitools.openapidiff.core.model.ChangedHeader;
import org.openapitools.openapidiff.core.model.ChangedHeaders;
import org.openapitools.openapidiff.core.model.ChangedList;
import org.openapitools.openapidiff.core.model.ChangedMediaType;
import org.openapitools.openapidiff.core.model.ChangedMetadata;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;
import org.openapitools.openapidiff.core.model.ChangedParameter;
import org.openapitools.openapidiff.core.model.ChangedParameters;
import org.openapitools.openapidiff.core.model.ChangedRequestBody;
import org.openapitools.openapidiff.core.model.ChangedResponse;
import org.openapitools.openapidiff.core.model.ChangedSchema;
import org.openapitools.openapidiff.core.model.ChangedSecurityRequirement;
import org.openapitools.openapidiff.core.model.ChangedSecurityRequirements;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.Endpoint;
import org.openapitools.openapidiff.core.model.schema.ChangedOneOfSchema;
import org.openapitools.openapidiff.core.model.schema.ChangedOperationId;
import org.openapitools.openapidiff.core.utils.RefPointer;
import org.openapitools.openapidiff.core.utils.RefType;

public class HtmlRender implements Render {

  private static final RefPointer<Schema<?>> refPointer = new RefPointer<>(RefType.SCHEMAS);
  public static final String COMMENT = "comment";
  public static final String MISSING = "missing";

  private final String title;
  private final String linkCss;
  private final boolean showAllChanges;
  protected ChangedOpenApi diff;

  public HtmlRender() {
    this("Api Change Log", "http://deepoove.com/swagger-diff/stylesheets/demo.css");
  }

  public HtmlRender(boolean showAllChanges) {
    this("Api Change Log", "http://deepoove.com/swagger-diff/stylesheets/demo.css", showAllChanges);
  }

  public HtmlRender(String title, String linkCss) {
    this.title = title;
    this.linkCss = linkCss;
    this.showAllChanges = false;
  }

  public HtmlRender(String title, String linkCss, boolean showAllChanges) {
    this.title = title;
    this.linkCss = linkCss;
    this.showAllChanges = showAllChanges;
  }

  public void render(ChangedOpenApi diff, OutputStreamWriter outputStreamWriter) {
    this.diff = diff;

    List<Endpoint> newEndpoints = diff.getNewEndpoints();
    OlTag ol_newEndpoint = ol_newEndpoint(newEndpoints);

    List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
    OlTag ol_missingEndpoint = ol_missingEndpoint(missingEndpoints);

    List<Endpoint> deprecatedEndpoints = diff.getDeprecatedEndpoints();
    OlTag ol_deprecatedEndpoint = ol_deprecatedEndpoint(deprecatedEndpoints);

    List<ChangedOperation> changedOperations = diff.getChangedOperations();
    OlTag ol_changed = ol_changed(changedOperations);

    renderHtml(
        ol_newEndpoint, ol_missingEndpoint, ol_deprecatedEndpoint, ol_changed, outputStreamWriter);
  }

  public void renderHtml(
      OlTag ol_new,
      OlTag ol_miss,
      OlTag ol_deprec,
      OlTag ol_changed,
      OutputStreamWriter outputStreamWriter) {
    HtmlTag html =
        html()
            .attr("lang", "en")
            .with(
                head()
                    .with(
                        meta().withCharset("utf-8"),
                        title(title),
                        link().withRel("stylesheet").withHref(linkCss)),
                body()
                    .with(
                        header().with(h1(title), specSubtitle()),
                        div()
                            .withClass("article")
                            .with(
                                div().with(h2("What's New"), hr(), ol_new),
                                div().with(h2("What's Deleted"), hr(), ol_miss),
                                div().with(h2("What's Deprecated"), hr(), ol_deprec),
                                div().with(h2("What's Changed"), hr(), ol_changed),
                                resultSection())));

    try {
      FlatHtml<OutputStreamWriter> flatHtml = FlatHtml.into(outputStreamWriter);
      document().render(flatHtml);
      html.render(flatHtml);
      outputStreamWriter.close();
    } catch (IOException e) {
      throw new RendererException("Problem rendering html document.", e);
    }
  }

  private ContainerTag<?> specSubtitle() {
    if (diff == null) {
      return p();
    }
    String specTitle = diff.getNewSpecOpenApi().getInfo().getTitle();
    String version = diff.getNewSpecOpenApi().getInfo().getVersion();
    String subtitle =
        (StringUtils.isBlank(specTitle) ? "" : specTitle)
            + " (v "
            + (StringUtils.isBlank(version) ? "" : version)
            + ")";
    return p(subtitle.trim());
  }

  private ContainerTag<?> resultSection() {
    if (diff == null) {
      return div();
    }
    return div().with(h2("Result"), hr(), p(resultSummary()));
  }

  private String resultSummary() {
    if (diff.isUnchanged()) {
      return "No differences. Specifications are equivalent";
    }
    if (diff.isCompatible()) {
      return "API changes are backward compatible";
    }
    return "API changes broke backward compatibility";
  }

  private OlTag ol_newEndpoint(List<Endpoint> endpoints) {
    if (null == endpoints) return ol();
    OlTag ol = ol();
    for (Endpoint endpoint : endpoints) {
      ol.with(
          li_newEndpoint(
              endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
    }
    return ol;
  }

  private LiTag li_newEndpoint(String method, String path, String desc) {
    return li().with(span(method).withClass(method)).withText(path + " ").with(span(desc));
  }

  private OlTag ol_missingEndpoint(List<Endpoint> endpoints) {
    if (null == endpoints) return ol();
    OlTag ol = ol();
    for (Endpoint endpoint : endpoints) {
      ol.with(
          li_missingEndpoint(
              endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
    }
    return ol;
  }

  private LiTag li_missingEndpoint(String method, String path, String desc) {
    return li().with(span(method).withClass(method), del().withText(path)).with(span(" " + desc));
  }

  private OlTag ol_deprecatedEndpoint(List<Endpoint> endpoints) {
    if (null == endpoints) return ol();
    OlTag ol = ol();
    for (Endpoint endpoint : endpoints) {
      ol.with(
          li_deprecatedEndpoint(
              endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
    }
    return ol;
  }

  private LiTag li_deprecatedEndpoint(String method, String path, String desc) {
    return li().with(span(method).withClass(method), del().withText(path)).with(span(" " + desc));
  }

  private OlTag ol_changed(List<ChangedOperation> changedOperations) {
    if (null == changedOperations) return ol();
    OlTag ol = ol();
    for (ChangedOperation changedOperation : changedOperations) {
      String pathUrl = changedOperation.getPathUrl();
      String method = changedOperation.getHttpMethod().toString();
      String desc =
          Optional.ofNullable(changedOperation.getSummary())
              .map(ChangedMetadata::getRight)
              .orElse("");

      UlTag ul_detail = ul().withClass("detail");
      if (result(changedOperation.getOperationId()).isDifferent()) {
        ul_detail.with(
            li().with(h3("Operation ID")).with(ul_operation_id(changedOperation.getOperationId())));
      }
      if (result(changedOperation.getParameters()).isDifferent()) {
        ul_detail.with(
            li().with(h3("Parameters")).with(ul_param(changedOperation.getParameters())));
      }
      if (changedOperation.resultRequestBody().isDifferent()) {
        ul_detail.with(li_request(changedOperation.getRequestBody()));
      }
      if (changedOperation.resultApiResponses().isDifferent()) {
        ul_detail.with(
            li().with(h3("Response")).with(ul_response(changedOperation.getApiResponses())));
      }
      if (showAllChanges && changedOperation.resultSecurityRequirements().isDifferent()) {
        ul_detail.with(
            li().with(h3("Security Requirements"))
                .with(ul_securityRequirements(changedOperation.getSecurityRequirements())));
      }
      ol.with(
          li().with(span(method).withClass(method))
              .withText(pathUrl + " ")
              .with(span(desc))
              .with(ul_detail));
    }
    return ol;
  }

  private LiTag li_request(ChangedRequestBody requestBody) {
    LiTag li = li().with(h3("Request"));
    ChangedMetadata description = requestBody.getDescription();
    if (description != null && StringUtils.isNotBlank(description.getRight())) {
      li.with(span("//" + description.getRight().trim()).withClass(COMMENT));
    }
    return li.with(ul_request(requestBody.getContent()));
  }

  private UlTag ul_securityRequirements(ChangedSecurityRequirements changedSecurityRequirements) {
    List<SecurityRequirement> addRequirements = changedSecurityRequirements.getIncreased();
    List<SecurityRequirement> delRequirements = changedSecurityRequirements.getMissing();
    List<ChangedSecurityRequirement> changedRequirements = changedSecurityRequirements.getChanged();
    UlTag ul = ul().withClass("change security requirements");
    if (addRequirements != null) {
      for (SecurityRequirement addRequirement : addRequirements) {
        ul.with(li_addSecurityRequirement(addRequirement));
      }
    }
    if (delRequirements != null) {
      for (SecurityRequirement delRequirement : delRequirements) {
        ul.with(li_missingSecurityRequirement(delRequirement));
      }
    }
    if (changedRequirements != null) {
      for (ChangedSecurityRequirement changedRequirement : changedRequirements) {
        ul.with(li_changedSecurityRequirement(changedRequirement));
      }
    }

    return ul;
  }

  private LiTag li_addSecurityRequirement(SecurityRequirement securityRequirement) {
    return li().withText("New security requirement : ")
        .with(span(null == securityRequirement.toString() ? "" : (securityRequirement.toString())));
  }

  private LiTag li_missingSecurityRequirement(SecurityRequirement securityRequirement) {
    return li().withText("Deleted security requirement : ")
        .with(span(null == securityRequirement.toString() ? "" : (securityRequirement.toString())));
  }

  private LiTag li_changedSecurityRequirement(
      ChangedSecurityRequirement changedSecurityRequirement) {
    return li().withText(String.format("Changed security requirement : "))
        .with(
            span(
                (null == changedSecurityRequirement.getNewSecurityRequirement()
                        || null
                            == changedSecurityRequirement.getNewSecurityRequirement().toString())
                    ? ""
                    : (changedSecurityRequirement.getNewSecurityRequirement().toString())));
  }

  private UlTag ul_response(ChangedApiResponse changedApiResponse) {
    Map<String, ApiResponse> addResponses = changedApiResponse.getIncreased();
    Map<String, ApiResponse> delResponses = changedApiResponse.getMissing();
    Map<String, ChangedResponse> changedResponses = changedApiResponse.getChanged();
    UlTag ul = ul().withClass("change response");
    for (Entry<String, ApiResponse> prop : addResponses.entrySet()) {
      ul.with(li_addResponse(prop.getKey(), prop.getValue()));
    }
    for (Entry<String, ApiResponse> prop : delResponses.entrySet()) {
      ul.with(li_missingResponse(prop.getKey(), prop.getValue()));
    }
    for (Entry<String, ChangedResponse> prop : changedResponses.entrySet()) {
      ul.with(li_changedResponse(prop.getKey(), prop.getValue()));
    }
    return ul;
  }

  private String statusCode(String code) {
    if (code.equals("default") || code.matches("[1-5]XX")) {
      return code;
    }
    return code + " " + HttpStatus.getReasonPhrase(Integer.parseInt(code));
  }

  private LiTag li_addResponse(String name, ApiResponse response) {
    return li().withText(String.format("New response : [%s]", statusCode(name)))
        .with(
            span(null == response.getDescription() ? "" : ("//" + response.getDescription()))
                .withClass(COMMENT));
  }

  private LiTag li_missingResponse(String name, ApiResponse response) {
    return li().withText(String.format("Deleted response : [%s]", statusCode(name)))
        .with(
            span(null == response.getDescription() ? "" : ("//" + response.getDescription()))
                .withClass(COMMENT));
  }

  private LiTag li_changedResponse(String name, ChangedResponse response) {
    LiTag li =
        li().withText(String.format("Changed response : [%s]", statusCode(name)))
            .with(
                span((null == response.getNewApiResponse()
                            || null == response.getNewApiResponse().getDescription())
                        ? ""
                        : ("//" + response.getNewApiResponse().getDescription()))
                    .withClass(COMMENT));
    if (response.getHeaders() != null) {
      li.with(ul_headers(response.getHeaders()));
    }
    return li.with(ul_request(response.getContent()));
  }

  private UlTag ul_headers(ChangedHeaders changedHeaders) {
    UlTag ul = ul().withClass("change headers");
    if (changedHeaders == null) {
      return ul;
    }
    Map<String, Header> addHeaders = changedHeaders.getIncreased();
    Map<String, Header> delHeaders = changedHeaders.getMissing();
    Map<String, ChangedHeader> changedHeadersMap = changedHeaders.getChanged();
    if (addHeaders != null) {
      for (Entry<String, Header> header : addHeaders.entrySet()) {
        ul.with(li_addHeader(header.getKey(), header.getValue()));
      }
    }
    if (delHeaders != null) {
      for (Entry<String, Header> header : delHeaders.entrySet()) {
        ul.with(li_missingHeader(header.getKey(), header.getValue()));
      }
    }
    if (changedHeadersMap != null) {
      for (Entry<String, ChangedHeader> header : changedHeadersMap.entrySet()) {
        ul.with(li_changedHeader(header.getKey(), header.getValue()));
      }
    }
    return ul;
  }

  private LiTag li_addHeader(String name, Header header) {
    return li().withText(String.format("New header : %s", name))
        .with(
            span(null == header.getDescription() ? "" : ("//" + header.getDescription()))
                .withClass(COMMENT));
  }

  private LiTag li_missingHeader(String name, Header header) {
    return li().withText(String.format("Deleted header : %s", name))
        .with(
            span(null == header.getDescription() ? "" : ("//" + header.getDescription()))
                .withClass(COMMENT));
  }

  private LiTag li_changedHeader(String name, ChangedHeader header) {
    return li().withText(String.format("Changed header : %s", name))
        .with(
            span((null == header.getNewHeader() || null == header.getNewHeader().getDescription())
                    ? ""
                    : ("//" + header.getNewHeader().getDescription()))
                .withClass(COMMENT));
  }

  private UlTag ul_request(ChangedContent changedContent) {
    UlTag ul = ul().withClass("change request-body");
    if (changedContent != null) {
      for (String propName : changedContent.getIncreased().keySet()) {
        ul.with(li_addRequest(propName, changedContent.getIncreased().get(propName)));
      }
      for (String propName : changedContent.getMissing().keySet()) {
        ul.with(li_missingRequest(propName, changedContent.getMissing().get(propName)));
      }
      for (String propName : changedContent.getChanged().keySet()) {
        ul.with(li_changedRequest(propName, changedContent.getChanged().get(propName)));
      }
    }
    return ul;
  }

  private LiTag li_addRequest(String name, MediaType request) {
    return li().withText(String.format("New body: '%s'", name));
  }

  private LiTag li_missingRequest(String name, MediaType request) {
    return li().withText(String.format("Deleted body: '%s'", name));
  }

  private LiTag li_changedRequest(String name, ChangedMediaType request) {
    LiTag li = li().withText(String.format("Changed body: '%s'", name));
    ChangedSchema schema = request.getSchema();
    if (schema != null) {
      li.with(div_changedSchema(schema));
      if (showAllChanges) {
        allChanges(li, schema);
      } else {
        incompatibilities(li, schema);
      }
    }
    return li;
  }

  private DivTag div_changedSchema(ChangedSchema schema) {
    DivTag div = div();
    div.with(h3("Schema" + (schema.isIncompatible() ? " incompatible" : "")));
    return div;
  }

  private void allChanges(final LiTag output, final ChangedSchema schema) {
    allChanges(output, "", schema);
  }

  private void allChanges(
      final ContainerTag<?> output, String propName, final ChangedSchema schema) {
    String prefix = propName.isEmpty() ? "" : propName + ".";
    oneOfSchema(output, schema.getOneOfSchema(), discriminator(schema));
    if (schema.getRequired() != null) {
      requiredProperties(
          output, prefix, "New required properties", schema.getRequired().getIncreased());
      requiredProperties(
          output, prefix, "New optional properties", schema.getRequired().getMissing());
    }
    if (schema.getItems() != null) {
      itemsAllChanges(output, propName, schema.getItems());
    }
    listDiff(output, "enum value", schema.getEnumeration());
    properties(
        output, prefix, "Added property", schema.getIncreasedProperties(), schema.getContext());
    properties(
        output, prefix, "Deleted property", schema.getMissingProperties(), schema.getContext());
    schema
        .getChangedProperties()
        .forEach((name, property) -> property(output, prefix + name, property));
  }

  private void incompatibilities(final LiTag output, final ChangedSchema schema) {
    incompatibilities(output, "", schema);
  }

  private void incompatibilities(
      final ContainerTag<?> output, String propName, final ChangedSchema schema) {
    String prefix = propName.isEmpty() ? "" : propName + ".";
    oneOfSchema(output, schema.getOneOfSchema(), discriminator(schema));
    if (schema.getRequired() != null) {
      requiredProperties(
          output, prefix, "New required properties", schema.getRequired().getIncreased());
      requiredProperties(
          output, prefix, "New optional properties", schema.getRequired().getMissing());
    }
    if (schema.getItems() != null) {
      items(output, propName, schema.getItems());
    }
    listDiff(output, "enum value", schema.getEnumeration());
    properties(
        output, prefix, "Added property", schema.getIncreasedProperties(), schema.getContext());
    properties(
        output, prefix, "Deleted property", schema.getMissingProperties(), schema.getContext());
    schema
        .getChangedProperties()
        .forEach((name, property) -> property(output, prefix + name, property));
  }

  private void items(ContainerTag<?> output, String propName, ChangedSchema schema) {
    changedItems(output, propName, schema);
    incompatibilities(output, propName + "[n]", schema);
  }

  private void itemsAllChanges(ContainerTag<?> output, String propName, ChangedSchema schema) {
    changedItems(output, propName, schema);
    allChanges(output, propName + "[n]", schema);
  }

  private void changedItems(ContainerTag<?> output, String propName, ChangedSchema schema) {
    property(output, propName + "[n]", "Changed items", changedType(schema), description(schema));
  }

  private void requiredProperties(
      ContainerTag<?> output, String propPrefix, String title, List<String> properties) {
    if (properties != null) {
      properties.forEach(name -> property(output, propPrefix + name, title, ""));
    }
  }

  private void listDiff(ContainerTag<?> output, String name, ChangedList<?> listDiff) {
    if (listDiff == null) {
      return;
    }
    listItem(output, "Added " + name, listDiff.getIncreased());
    listItem(output, "Removed " + name, listDiff.getMissing());
  }

  private <T> void listItem(ContainerTag<?> output, String title, List<T> values) {
    if (values != null) {
      values.forEach(value -> property(output, String.valueOf(value), title, ""));
    }
  }

  private void oneOfSchema(
      ContainerTag<?> output, ChangedOneOfSchema oneOfSchema, String discriminator) {
    if (oneOfSchema == null) {
      return;
    }
    if (oneOfSchema.getMissing() != null) {
      oneOfSchema
          .getMissing()
          .keySet()
          .forEach(
              key ->
                  valueLine(output, String.format("Deleted '%s' %s", key, discriminator).trim()));
    }
    if (oneOfSchema.getIncreased() != null) {
      oneOfSchema
          .getIncreased()
          .forEach(
              (key, sub) ->
                  valueLine(
                      output,
                      String.format(
                              "Added '%s' %s (%s)", key, discriminator, resolveTypeQuietly(sub))
                          .trim()));
    }
    if (oneOfSchema.getChanged() != null) {
      oneOfSchema
          .getChanged()
          .forEach(
              (key, sub) -> {
                valueLine(output, String.format("Updated '%s' %s", key, discriminator).trim());
                allChanges(output, "", sub);
              });
    }
  }

  private String resolveTypeQuietly(Schema<?> schema) {
    try {
      return type(resolve(schema));
    } catch (Exception e) {
      return type(schema);
    }
  }

  private void valueLine(ContainerTag<?> output, String text) {
    output.with(p(text).withClass(MISSING));
  }

  private String discriminator(ChangedSchema schema) {
    if (schema.getNewSchema() != null && schema.getNewSchema().getDiscriminator() != null) {
      return schema.getNewSchema().getDiscriminator().getPropertyName();
    }
    return "";
  }

  private void properties(
      ContainerTag<?> output,
      String propPrefix,
      String title,
      Map<String, Schema<?>> properties,
      DiffContext context) {
    if (properties != null) {
      properties.forEach((key, value) -> resolveProperty(output, propPrefix, key, value, title));
    }
  }

  private void resolveProperty(
      ContainerTag<?> output, String propPrefix, String key, Schema<?> value, String title) {
    try {
      property(output, propPrefix + key, title, type(resolve(value)), value.getDescription());
    } catch (Exception e) {
      property(output, propPrefix + key, title, type(value), value.getDescription());
    }
  }

  protected void property(ContainerTag<?> output, String name, String title, Schema<?> schema) {
    property(output, name, title, type(schema), schema.getDescription());
  }

  protected void property(ContainerTag<?> output, String name, String title, String type) {
    property(output, name, title, type, "");
  }

  protected void property(ContainerTag<?> output, String name, ChangedSchema schema) {
    property(output, name, "Changed property", changedType(schema), description(schema));
    allChanges(output, name, schema);
  }

  protected void property(
      ContainerTag<?> output, String name, String title, String type, String description) {
    String label = String.format("%s: %s", title, name);
    if (!type.isEmpty()) {
      label += String.format(" (%s)", type);
    }
    PTag line = p(label).withClass(MISSING);
    if (StringUtils.isNotBlank(description)) {
      line.with(span("//" + description).withClass(COMMENT));
    }
    output.with(line);
  }

  protected String changedType(ChangedSchema schema) {
    String newType = type(schema.getNewSchema());
    if (schema.isChangedType()) {
      return type(schema.getOldSchema()) + " -> " + newType;
    }
    return newType;
  }

  protected String description(ChangedSchema schema) {
    try {
      return description(resolve(schema));
    } catch (Exception e) {
      return "";
    }
  }

  protected String description(Schema<?> schema) {
    return null == schema || null == schema.getDescription() ? "" : schema.getDescription();
  }

  protected Schema<?> resolve(Schema<?> schema) {
    return refPointer.resolveRef(
        diff.getNewSpecOpenApi().getComponents(), schema, schema.get$ref());
  }

  protected Schema<?> resolve(ChangedSchema schema) {
    return refPointer.resolveRef(
        diff.getNewSpecOpenApi().getComponents(),
        schema.getNewSchema(),
        schema.getNewSchema().get$ref());
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

  protected String type(ChangedSchema schema) {
    return type(schema.getNewSchema());
  }

  private UlTag ul_param(ChangedParameters changedParameters) {
    List<Parameter> addParameters = changedParameters.getIncreased();
    List<Parameter> delParameters = changedParameters.getMissing();
    List<ChangedParameter> changed = changedParameters.getChanged();
    UlTag ul = ul().withClass("change param");
    for (Parameter param : addParameters) {
      ul.with(li_addParam(param));
    }
    for (ChangedParameter param : changed) {
      ul.with(li_changedParam(param));
    }
    for (Parameter param : delParameters) {
      ul.with(li_missingParam(param));
    }
    return ul;
  }

  private LiTag li_addParam(Parameter param) {
    return li().withText("Add " + param.getName() + " in " + param.getIn())
        .with(
            span(null == param.getDescription() ? "" : ("//" + param.getDescription()))
                .withClass(COMMENT));
  }

  private LiTag li_missingParam(Parameter param) {
    return li().withClass(MISSING)
        .with(span("Delete"))
        .with(del(param.getName()))
        .with(span("in ").withText(param.getIn()))
        .with(
            span(null == param.getDescription() ? "" : ("//" + param.getDescription()))
                .withClass(COMMENT));
  }

  private LiTag li_deprecatedParam(ChangedParameter param) {
    return li().withClass(MISSING)
        .with(span("Deprecated"))
        .with(del(param.getName()))
        .with(span("in ").withText(param.getIn()))
        .with(
            span(null == param.getNewParameter().getDescription()
                    ? ""
                    : ("//" + param.getNewParameter().getDescription()))
                .withClass(COMMENT));
  }

  private LiTag li_changedParam(ChangedParameter changeParam) {
    if (changeParam.isDeprecated()) {
      return li_deprecatedParam(changeParam);
    }
    boolean changeRequired = changeParam.isChangeRequired();
    boolean changeDescription =
        Optional.ofNullable(changeParam.getDescription())
            .map(ChangedMetadata::isDifferent)
            .orElse(false);
    Parameter rightParam = changeParam.getNewParameter();
    Parameter leftParam = changeParam.getOldParameter();
    LiTag li = li().withText(changeParam.getName() + " in " + changeParam.getIn());
    if (changeRequired) {
      li.withText(
          " change into "
              + (rightParam.getRequired() != null && rightParam.getRequired()
                  ? "required"
                  : "not required"));
    }
    if (changeDescription) {
      li.withText(" Notes ")
          .with(del(leftParam.getDescription()).withClass(COMMENT))
          .withText(" change into ")
          .with(span(rightParam.getDescription()).withClass(COMMENT));
    }
    return li;
  }

  private UlTag ul_operation_id(ChangedOperationId changedOperationId) {
    return ul().withClass("change")
        .with(
            li().withText(
                    "Changed "
                        + Optional.ofNullable(changedOperationId.getLeft()).orElse("")
                        + " to "
                        + Optional.ofNullable(changedOperationId.getRight()).orElse("")));
  }
}
