package com.qdesrame.openapi.diff.output;

import com.qdesrame.openapi.diff.model.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class ConsoleRender implements Render {
    private static final int LINE_LENGTH = 74;

    private StringBuilder output;

    @Override
    public String render(ChangedOpenApi diff) {
        output = new StringBuilder();
        if (!diff.isDiff()) {
            output.append("No differences. Specifications are equivalents");
        } else {
            output.append(bigTitle("Api Change Log"))
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
                    .append(StringUtils.center(diff.isDiffBackwardCompatible() ?
                            "API changes are backward compatible" : "API changes broke backward compatibility", LINE_LENGTH))
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
            String desc = operation.getSummary();

            StringBuilder ul_detail = new StringBuilder();
            if (operation.isDiffParam()) {
                ul_detail.append(StringUtils.repeat(' ', 2)).append("Parameter:")
                        .append(System.lineSeparator())
                        .append(ul_param(operation.getChangedParameters()));
            }
            if (operation.isDiffRequest()) {
                ul_detail.append(StringUtils.repeat(' ', 2)).append("Request:")
                        .append(System.lineSeparator())
                        .append(ul_content(operation.getRequestChangedContent(), true));
            }
            if (operation.isDiffResponse()) {
                ul_detail.append(StringUtils.repeat(' ', 2)).append("Return Type:")
                        .append(System.lineSeparator())
                        .append(ul_response(operation.getChangedApiResponse()));
            }
            sb.append(itemEndpoint(method, pathUrl, desc)).append(ul_detail);
        }
        return sb.toString();
    }

    private String ul_response(ChangedApiResponse changedApiResponse) {
        Map<String, ApiResponse> addResponses = changedApiResponse.getAddResponses();
        Map<String, ApiResponse> delResponses = changedApiResponse.getMissingResponses();
        Map<String, ChangedResponse> changedResponses = changedApiResponse.getChangedResponses();
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
        sb.append(StringUtils.repeat(' ', 4)).append("- ").append(title).append(code)
                .append(' ').append(status).append(System.lineSeparator());
        return sb.toString();
    }

    private String itemChangedResponse(String title, String contentType, ChangedResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append(itemResponse(title, contentType));
        sb.append(StringUtils.repeat(' ', 6)).append("Media types:").append(System.lineSeparator());
        sb.append(ul_content(response.getChangedContent(), false));
        return sb.toString();
    }

    private String ul_content(ChangedContent changedContent, boolean isRequest) {
        StringBuilder sb = new StringBuilder();
        for (String propName : changedContent.getIncreased().keySet()) {
            sb.append(itemContent("Added ", propName));
        }
        for (String propName : changedContent.getMissing().keySet()) {
            sb.append(itemContent("Deleted ", propName));
        }
        for (String propName : changedContent.getChanged().keySet()) {
            sb.append(itemContent("Changed ", propName, changedContent.getChanged().get(propName), isRequest));
        }
        return sb.toString();
    }

    private String itemContent(String title, String contentType) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.repeat(' ', 8)).append("- ").append(title).append(contentType)
                .append(System.lineSeparator());
        return sb.toString();
    }

    private String itemContent(String title, String contentType, ChangedMediaType changedMediaType, boolean isRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append(itemContent(title, contentType))
                .append(StringUtils.repeat(' ', 10))
                .append("Schema: ")
                .append(changedMediaType.isDiffBackwardCompatible(isRequest) ?
                        "Backward compatible" : "Broken compatibility")
                .append(System.lineSeparator());
        return sb.toString();
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
        sb.append(StringUtils.repeat(' ', 4)).append("- ").append(title).append(param.getName())
                .append(" in ").append(param.getIn()).append(System.lineSeparator());
//                .append(null == param.getDescription() ? ""
//                        : (" //" + param.getDescription()));
        return sb.toString();
    }

    private String li_changedParam(ChangedParameter changeParam) {
        if (changeParam.isDeprecated()) {
            return itemParam("Deprecated ", changeParam.getRightParameter());
        } else {
            return itemParam("Changed ", changeParam.getRightParameter());
        }
    }

    private String listEndpoints(List<Endpoint> endpoints, String title) {
        if (null == endpoints || endpoints.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(title(title));
        for (Endpoint endpoint : endpoints) {
            sb.append(itemEndpoint(endpoint.getMethod().toString(),
                    endpoint.getPathUrl(), endpoint.getSummary()));
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
        sb.append(ol_new)
                .append(ol_miss)
                .append(ol_deprec)
                .append(ol_changed);
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
        return String.format("%s%s%s%s%n%s", separator(ch), little, StringUtils.center(title, LINE_LENGTH - 4), little, separator(ch));
    }

    public StringBuilder separator(char ch) {
        StringBuilder sb = new StringBuilder();
        return sb.append(StringUtils.repeat(ch, LINE_LENGTH)).append(System.lineSeparator());
    }

}
