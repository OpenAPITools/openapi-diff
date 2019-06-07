package com.qdesrame.openapi.diff.output;

import static com.qdesrame.openapi.diff.model.Changed.result;
import static j2html.TagCreator.*;

import com.qdesrame.openapi.diff.model.*;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import j2html.tags.ContainerTag;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HtmlRender implements Render {

  private String title;
  private String linkCss;

  private SimpleDateFormat dateChangesFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

  public HtmlRender() {
    // this("Api Change Log", "http://deepoove.com/swagger-diff/stylesheets/demo.css");
    this("Api Change Log", "/diff.css");
  }

  public HtmlRender(String title, String linkCss) {
    this.title = title;
    this.linkCss = linkCss;
  }

  public String render(ChangedOpenApi diff) {
    List<Endpoint> newEndpoints = diff.getNewEndpoints();
    ContainerTag ol_newEndpoint = ol_newEndpoint(newEndpoints);

    List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
    ContainerTag ol_missingEndpoint = ol_missingEndpoint(missingEndpoints);

    List<Endpoint> deprecatedEndpoints = diff.getDeprecatedEndpoints();
    ContainerTag ol_deprecatedEndpoint = ol_deprecatedEndpoint(deprecatedEndpoints);

    List<ChangedOperation> changedOperations = diff.getChangedOperations();
    ContainerTag ol_changed = ol_changed(changedOperations);

    ContainerTag ol_compatible = ol_compatible(diff.isCompatible());

    return renderHtml(
        ol_newEndpoint, ol_missingEndpoint, ol_deprecatedEndpoint, ol_changed, ol_compatible);
  }

  public String renderHtml(
      ContainerTag ol_new,
      ContainerTag ol_miss,
      ContainerTag ol_deprec,
      ContainerTag ol_changed,
      ContainerTag compatible) {
    ContainerTag html =
        html()
            .attr("lang", "en")
            .with(
                head()
                    .with(
                        meta().withCharset("utf-8"),
                        title(title + " " + dateChangesFormatter.format(new Date())),
                        link().withRel("stylesheet").withHref(linkCss)),
                body()
                    .with(
                        header().with(h1(title + " " + dateChangesFormatter.format(new Date()))),
                        div()
                            .withClass("article")
                            .with(
                                div().with(h2("What's New"), hr(), ol_new),
                                div().with(h2("What's Deleted"), hr(), ol_miss),
                                div().with(h2("What's Deprecated"), hr(), ol_deprec),
                                div().with(h2("What's Changed"), hr(), ol_changed),
                                div().with(h2("Result"), hr(), compatible))));

    return document().render() + html.render();
  }

  private ContainerTag ol_newEndpoint(List<Endpoint> endpoints) {
    if (null == endpoints) return ol();
    ContainerTag ol = ol();
    for (Endpoint endpoint : endpoints) {
      ol.with(
          li_newEndpoint(
              endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
    }
    return ol;
  }

  private ContainerTag li_newEndpoint(String method, String path, String desc) {
    return li().with(span(method).withClass(method)).withText(path + " ").with(span(desc));
  }

  private ContainerTag ol_missingEndpoint(List<Endpoint> endpoints) {
    if (null == endpoints) return ol();
    ContainerTag ol = ol();
    for (Endpoint endpoint : endpoints) {
      ol.with(
          li_missingEndpoint(
              endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
    }
    return ol;
  }

  private ContainerTag li_missingEndpoint(String method, String path, String desc) {
    return li().with(span(method).withClass(method), del().withText(path)).with(span(" " + desc));
  }

  private ContainerTag ol_deprecatedEndpoint(List<Endpoint> endpoints) {
    if (null == endpoints) return ol();
    ContainerTag ol = ol();
    for (Endpoint endpoint : endpoints) {
      ol.with(
          li_deprecatedEndpoint(
              endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
    }
    return ol;
  }

  private ContainerTag li_deprecatedEndpoint(String method, String path, String desc) {
    return li().with(span(method).withClass(method), del().withText(path)).with(span(" " + desc));
  }

  private ContainerTag ol_changed(List<ChangedOperation> changedOperations) {
    if (null == changedOperations) return ol();
    ContainerTag ol = ol();
    for (ChangedOperation changedOperation : changedOperations) {
      String pathUrl = changedOperation.getPathUrl();
      String method = changedOperation.getHttpMethod().toString();
      String desc =
          Optional.ofNullable(changedOperation.getSummary())
              .map(ChangedMetadata::getRight)
              .orElse("");

      ContainerTag ul_detail = ul().withClass("detail");
      if (result(changedOperation.getParameters()).isDifferent()) {
        ul_detail.with(
            li().with(h3("Parameters")).with(ul_param(changedOperation.getParameters())));
      }
      if (changedOperation.resultRequestBody().isDifferent()) {
        ul_detail.with(
            li().with(h3("Request"))
                .with(ul_request(changedOperation.getRequestBody().getContent())));
      } else {
      }
      if (changedOperation.resultApiResponses().isDifferent()) {
        ul_detail.with(
            li().with(h3("Response")).with(ul_response(changedOperation.getApiResponses())));
      }
      ol.with(
          li().with(span(method).withClass(method))
              .withText(pathUrl + " ")
              .with(span(desc))
              .with(ul_detail));
    }
    return ol;
  }

  private ContainerTag ol_compatible(boolean compatible) {
    return ol().with(
            li().with(
                    ul().withClass("detail")
                        .with(
                            h3(
                                compatible
                                    ? "API changes are backward compatible"
                                    : "API changes broke backward compatibility"))));
  }

  private ContainerTag ul_response(ChangedApiResponse changedApiResponse) {
    Map<String, ApiResponse> addResponses = changedApiResponse.getIncreased();
    Map<String, ApiResponse> delResponses = changedApiResponse.getMissing();
    Map<String, ChangedResponse> changedResponses = changedApiResponse.getChanged();
    ContainerTag ul = ul().withClass("change response");
    for (String propName : addResponses.keySet()) {
      ul.with(li_addResponse(propName, addResponses.get(propName)));
    }
    for (String propName : delResponses.keySet()) {
      ul.with(li_missingResponse(propName, delResponses.get(propName)));
    }
    for (String propName : changedResponses.keySet()) {
      ul.with(li_changedResponse(propName, changedResponses.get(propName)));
    }
    return ul;
  }

  private ContainerTag li_addResponse(String name, ApiResponse response) {
    return li().withText(String.format("New response : [%s]", name))
        .with(
            span(null == response.getDescription() ? "" : ("//" + response.getDescription()))
                .withClass("comment"));
  }

  private ContainerTag li_missingResponse(String name, ApiResponse response) {
    return li().withText(String.format("Deleted response : [%s]", name))
        .with(
            span(null == response.getDescription() ? "" : ("//" + response.getDescription()))
                .withClass("comment"));
  }

  private ContainerTag li_changedResponse(String name, ChangedResponse response) {
    return li().withText(String.format("Changed response : [%s]", name))
        .with(
            span((null == response.getNewApiResponse()
                        || null == response.getNewApiResponse().getDescription())
                    ? ""
                    : ("//" + response.getNewApiResponse().getDescription()))
                .withClass("comment"))
        .with(ul_request(response.getContent()));
  }

  private ContainerTag ul_request(ChangedContent changedContent) {
    ContainerTag ul = ul().withClass("change request-body");
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

  private ContainerTag li_addRequest(String name, MediaType request) {
    return li().withText(String.format("New body: '%s'", name));
  }

  private ContainerTag li_missingRequest(String name, MediaType request) {
    return li().withText(String.format("Deleted body: '%s'", name));
  }

  private ContainerTag li_changedRequest(String name, ChangedMediaType request) {
    return li().withText(String.format("Changed body: '%s'", name))
        .with(div_changedSchema(request.getSchema()));
  }

  private ContainerTag div_changedSchema(ChangedSchema schema) {
    ContainerTag div = div();
    div.with(h2("!! Schema !!"));
    if (schema.GetChanged().size() > 0) {
      div.with(h3("changed properties"));
      schema.GetChanged().forEach((k, v) -> div.with(ul(k)));
    }
    if (schema.GetIncreased().size() > 0) {
      div.with(h3("increased properties"));
      schema.GetIncreased().forEach((k, v) -> div.with(ul(k)));
    }
    if (schema.GetMissing().size() > 0) {
      div.with(h3("missing properties"));
      schema.GetMissing().forEach((k, v) -> div.with(ul(k)));
    }
    return div;
  }

  private ContainerTag ul_param(ChangedParameters changedParameters) {
    List<Parameter> addParameters = changedParameters.getIncreased();
    List<Parameter> delParameters = changedParameters.getMissing();
    List<ChangedParameter> changed = changedParameters.getChanged();
    ContainerTag ul = ul().withClass("change param");
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

  private ContainerTag li_addParam(Parameter param) {
    return li().withText("Add " + param.getName() + " in " + param.getIn())
        .with(
            span(null == param.getDescription() ? "" : ("//" + param.getDescription()))
                .withClass("comment"));
  }

  private ContainerTag li_missingParam(Parameter param) {
    return li().withClass("missing")
        .with(span("Delete"))
        .with(del(param.getName()))
        .with(span("in ").withText(param.getIn()))
        .with(
            span(null == param.getDescription() ? "" : ("//" + param.getDescription()))
                .withClass("comment"));
  }

  private ContainerTag li_deprecatedParam(ChangedParameter param) {
    return li().withClass("missing")
        .with(span("Deprecated"))
        .with(del(param.getName()))
        .with(span("in ").withText(param.getIn()))
        .with(
            span(null == param.getNewParameter().getDescription()
                    ? ""
                    : ("//" + param.getNewParameter().getDescription()))
                .withClass("comment"));
  }

  private ContainerTag li_changedParam(ChangedParameter changeParam) {
    if (changeParam.isDeprecated()) {
      return li_deprecatedParam(changeParam);
    }
    boolean changeRequired = changeParam.isChangeRequired();
    boolean changeDescription = changeParam.getDescription().isDifferent();
    Parameter rightParam = changeParam.getNewParameter();
    Parameter leftParam = changeParam.getNewParameter();
    ContainerTag li = li().withText(changeParam.getName() + " in " + changeParam.getIn());
    if (changeRequired) {
      li.withText(" change into " + (rightParam.getRequired() ? "required" : "not required"));
    }
    if (changeDescription) {
      li.withText(" Notes ")
          .with(del(leftParam.getDescription()).withClass("comment"))
          .withText(" change into ")
          .with(span(rightParam.getDescription()).withClass("comment"));
    }
    return li;
  }
}
