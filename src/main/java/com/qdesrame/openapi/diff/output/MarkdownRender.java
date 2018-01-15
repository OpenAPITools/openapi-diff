package com.qdesrame.openapi.diff.output;

import com.qdesrame.openapi.diff.model.*;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class MarkdownRender implements Render {
    public static final Logger LOGGER = LoggerFactory.getLogger(MarkdownRender.class);

    private final String H3 = "### ";
    private final String H4 = "#### ";
    private final String H5 = "##### ";
    private final String BLOCKQUOTE = "> ";
    private final String CODE = "`";
    private final String PRE_CODE = "    ";
    private final String PRE_LI = "    ";
    private final String LI = "* ";
    private final String HR = "---\n";

    public MarkdownRender() {
    }

    public String render(ChangedOpenApi diff) {
        StringBuilder sb = new StringBuilder();
        sb.append(listEndpoints("What's New", diff.getNewEndpoints()))
                .append(listEndpoints("What's Deleted", diff.getMissingEndpoints()))
                .append(listEndpoints("What's Deprecated", diff.getDeprecatedEndpoints()))
                .append(H3).append("What's Changed").append("\n").append(HR)
                .append(ol_changed(diff.getChangedOperations()));
        return sb.toString();
    }

    private String listEndpoints(String title, List<Endpoint> endpoints) {
        if (null == endpoints || endpoints.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(H3).append(title).append("\n").append(HR);
        for (Endpoint endpoint : endpoints) {
            sb.append(itemEndpoint(endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
        }
        return sb.toString();
    }

    private String itemEndpoint(String method, String path, String summary) {
        StringBuilder sb = new StringBuilder();
        sb.append(H4).append(CODE).append(method).append(CODE).append(" ")
                .append(path).append("\n").append(description(summary)).append("  \n\n");
        /*.append(desc.replaceAll("\n", "\n> "))*/
        return sb.toString();
    }

    private String ol_changed(List<ChangedOperation> changedOperations) {
        if (null == changedOperations || changedOperations.size() == 0) return "N/A";
        StringBuilder sb = new StringBuilder();
        for (ChangedOperation changedOperation : changedOperations) {
            String pathUrl = changedOperation.getPathUrl();

            String method = changedOperation.getHttpMethod().toString();
            String desc = changedOperation.getSummary();

            StringBuilder ul_detail = new StringBuilder();
            if (changedOperation.isDiffParam()) {
                ul_detail.append(H5).append("Parameters: ")
                        .append(ul_param(changedOperation.getChangedParameters()));
            }
            if (changedOperation.isDiffRequest()) {
                ul_detail.append(H5).append("Request: ")
                        .append(bodyContent(changedOperation.getChangedRequestBody().getChangedContent()));
            }
            if (changedOperation.isDiffResponse()) {
                ul_detail.append(H5).append("Return Type: ")
                        .append(responses(changedOperation.getChangedApiResponse()));
            }
            sb.append(H4).append(CODE).append(method).append(CODE).append(" ").append(pathUrl)
                    .append('\n').append(description(desc)).append("\n").append(ul_detail);
        }
        return sb.toString();
    }

    private String responses(ChangedApiResponse changedApiResponse) {
        StringBuilder sb = new StringBuilder("\n\n");
        sb.append(listResponse("New response", changedApiResponse.getAddResponses()));
        sb.append(listResponse("Deleted response", changedApiResponse.getMissingResponses()));
        changedApiResponse.getChangedResponses().entrySet().stream().map(e -> this.itemResponse(e.getKey(), e.getValue())).forEach(sb::append);
        return sb.toString();
    }

    private String listResponse(String title, Map<String, ApiResponse> responses) {
        StringBuilder sb = new StringBuilder();
        responses.entrySet().stream().map(e -> this.itemResponse(title, e.getKey(), e.getValue())).forEach(sb::append);
        return sb.toString();
    }

    private String itemResponse(String title, String code, ApiResponse response) {
        return this.itemResponse(title, code, response.getDescription());
    }

    private String itemResponse(String code, ChangedResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.itemResponse("Changed response", code, null == response.getNewApiResponse() ?
                "" : response.getNewApiResponse().getDescription()));
        sb.append(headers(response.getChangedHeaders()));
        sb.append(this.bodyContent(LI, response.getChangedContent()));
        return sb.toString();
    }

    private String itemResponse(String title, String code, String description) {
        StringBuilder sb = new StringBuilder("");
        String status = "";
        if (!code.equals("default")) {
            status = HttpStatus.getStatusText(Integer.parseInt(code));
        }
        sb.append(String.format("%s : **%s %s**\n", title, code, status));
        sb.append(description(description));
        return sb.append("\n").toString();
    }

    private String headers(ChangedHeaders headers) {
        StringBuilder sb = new StringBuilder("");
        if (headers != null) {
            sb.append(listHeader("New header", headers.getIncreased()));
            sb.append(listHeader("Deleted header", headers.getMissing()));
            headers.getChanged().entrySet().stream().map(e -> this.itemHeader(e.getKey(), e.getValue())).forEach(sb::append);
        }
        return sb.toString();
    }

    private String listHeader(String title, Map<String, Header> headers) {
        StringBuilder sb = new StringBuilder();
        headers.entrySet().stream().map(e -> this.itemHeader(title, e.getKey(), e.getValue())).forEach(sb::append);
        return sb.toString();
    }

    private String itemHeader(String title, String name, Header header) {
        return this.itemHeader(title, name, header.getDescription());
    }

    private String itemHeader(String code, ChangedHeader header) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.itemHeader("Changed header", code, null == header.getNewHeader() ?
                "" : header.getNewHeader().getDescription()));
        return sb.toString();
    }

    private String itemHeader(String title, String mediaType, String description) {
        StringBuilder sb = new StringBuilder("");
        sb.append(String.format("%s : `%s`\n\n", title, mediaType));
        sb.append(description(description)).append('\n');
        return sb.toString();
    }

    private String bodyContent(String prefix, ChangedContent changedContent) {
        StringBuilder sb = new StringBuilder("\n\n");
        sb.append(listContent(prefix, "New content type", changedContent.getIncreased()));
        sb.append(listContent(prefix, "Deleted content type", changedContent.getMissing()));
        changedContent.getChanged().entrySet().stream().map(e -> this.itemContent(e.getKey(), e.getValue())).forEach(e -> sb.append(prefix).append(e));
        return sb.toString();
    }

    private String bodyContent(ChangedContent changedContent) {
        return bodyContent("", changedContent);
    }

    private String listContent(String prefix, String title, Map<String, MediaType> mediaTypes) {
        StringBuilder sb = new StringBuilder();
        mediaTypes.entrySet().stream().map(e -> this.itemContent(title, e.getKey(), e.getValue())).forEach(e -> sb.append(prefix).append(e));
        return sb.toString();
    }

    private String itemContent(String title, String mediaType) {
        StringBuilder sb = new StringBuilder("");
        sb.append(String.format("%s : `%s`\n\n", title, mediaType));
        return sb.toString();
    }

    private String itemContent(String title, String mediaType, MediaType content) {
        StringBuilder sb = new StringBuilder("");
        sb.append(itemContent(title, mediaType));
        return sb.toString();
    }

    private String itemContent(String mediaType, ChangedMediaType content) {
        StringBuilder sb = new StringBuilder("");
        sb.append(itemContent("Changed content type", mediaType));
        return sb.toString();
    }

    private String ul_param(ChangedParameters changedParameters) {
        List<Parameter> addParameters = changedParameters.getIncreased();
        List<Parameter> delParameters = changedParameters.getMissing();
        List<ChangedParameter> changed = changedParameters.getChanged();
        StringBuilder sb = new StringBuilder("\n\n");
        sb.append(parameters("Added", addParameters)).append(parameters("Deleted", delParameters));
        for (ChangedParameter param : changed) {
            boolean changeRequired = param.isChangeRequired();
            boolean changeDescription = param.isChangeDescription();
            if (changeRequired || changeDescription) sb.append(PRE_LI)
                    .append(PRE_CODE).append(li_changedParam(param)).append("\n").append(PRE_CODE);
        }
        return sb.toString();
    }

    private String parameters(String title, List<Parameter> parameters) {
        StringBuilder sb = new StringBuilder("");
        if (parameters.size() > 0) {
            for (Parameter parameter : parameters) {
                sb.append(String.format("%s: ", title)).append(code(parameter.getName()))
                        .append(" in ").append(code(parameter.getIn()))
                        .append(description(parameter.getDescription())).append('\n');
            }
        }
        return sb.toString();
    }

    private String li_changedParam(ChangedParameter changeParam) {
        boolean changeRequired = changeParam.isChangeRequired();
        boolean changeDescription = changeParam.isChangeDescription();
        Parameter rightParam = changeParam.getNewParameter();
        Parameter leftParam = changeParam.getNewParameter();
        StringBuilder sb = new StringBuilder("");
        sb.append(rightParam.getName());
        if (changeRequired) {
            sb.append(" change into " + (rightParam.getRequired() ? "required" : "not required"));
        }
        if (changeDescription) {
            sb.append(" Notes ").append(leftParam.getDescription()).append(" change into ")
                    .append(rightParam.getDescription());
        }
        return sb.toString();
    }

    private String code(String string) {
        StringBuilder sb = new StringBuilder();
        return sb.append(CODE).append(string).append(CODE).toString();
    }

    private String description(String description) {
        String result = "";
        if (StringUtils.isBlank(description)) {
            description = "";
        }
        if (!description.equals("")) {
            result = "> " + description.replaceAll("\n", "\n> ") + '\n';
        }
        return result;
    }
}
