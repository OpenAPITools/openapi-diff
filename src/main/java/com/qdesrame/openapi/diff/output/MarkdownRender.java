package com.qdesrame.openapi.diff.output;

import com.qdesrame.openapi.diff.model.*;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MarkdownRender implements Render {

    final String H3 = "### ";
    final String BLOCKQUOTE = "> ";
    final String CODE = "`";
    final String PRE_CODE = "    ";
    final String PRE_LI = "    ";
    final String LI = "* ";
    final String HR = "---\n";

    public MarkdownRender() {
    }

    public String render(ChangedOpenApi diff) {
        List<Endpoint> newEndpoints = diff.getNewEndpoints();
        String ol_newEndpoint = ol_newEndpoint(newEndpoints);

        List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
        String ol_missingEndpoint = ol_missingEndpoint(missingEndpoints);

        List<Endpoint> deprecatedEndpoints = diff.getDeprecatedEndpoints();
        String ol_deprecatedEndpoint = ol_deprecatedEndpoint(deprecatedEndpoints);

        List<ChangedEndpoint> changedEndpoints = diff.getChangedEndpoints();
        String ol_changed = ol_changed(changedEndpoints);

        return renderHtml(ol_newEndpoint, ol_missingEndpoint, ol_deprecatedEndpoint, ol_changed);
    }

    public String renderHtml(String ol_new, String ol_miss, String ol_deprec, String ol_changed) {
        StringBuffer sb = new StringBuffer();
        sb.append(H3).append("What's New").append("\n").append(HR)
                .append(ol_new).append("\n").append(H3)
                .append("What's Deleted").append("\n").append(HR)
                .append(ol_miss).append("\n").append(H3)
                .append("What's Deprecated").append("\n").append(HR)
                .append(ol_deprec).append("\n").append(H3)
                .append("What's Changed").append("\n").append(HR)
                .append(ol_changed);
        return sb.toString();
    }

    private String ol_newEndpoint(List<Endpoint> endpoints) {
        if (null == endpoints) return "";
        StringBuffer sb = new StringBuffer();
        for (Endpoint endpoint : endpoints) {
            sb.append(li_newEndpoint(endpoint.getMethod().toString(),
                    endpoint.getPathUrl(), endpoint.getSummary()));
        }
        return sb.toString();
    }

    private String li_newEndpoint(String method, String path, String desc) {
        StringBuffer sb = new StringBuffer();
        sb.append(LI).append(CODE).append(method).append(CODE)
                .append(" " + path).append(" " + desc + "\n");
        return sb.toString();
    }

    private String ol_missingEndpoint(List<Endpoint> endpoints) {
        if (null == endpoints) return "";
        StringBuffer sb = new StringBuffer();
        for (Endpoint endpoint : endpoints) {
            sb.append(li_newEndpoint(endpoint.getMethod().toString(),
                    endpoint.getPathUrl(), endpoint.getSummary()));
        }
        return sb.toString();
    }

    private String ol_deprecatedEndpoint(List<Endpoint> endpoints) {
        if (null == endpoints) return "";
        StringBuffer sb = new StringBuffer();
        for (Endpoint endpoint : endpoints) {
            sb.append(li_newEndpoint(endpoint.getMethod().toString(),
                    endpoint.getPathUrl(), endpoint.getSummary()));
        }
        return sb.toString();
    }

    private String ol_changed(List<ChangedEndpoint> changedEndpoints) {
        if (null == changedEndpoints) return "";
        StringBuffer sb = new StringBuffer();
        for (ChangedEndpoint changedEndpoint : changedEndpoints) {
            String pathUrl = changedEndpoint.getPathUrl();
            Map<PathItem.HttpMethod, ChangedOperation> changedOperations = changedEndpoint
                    .getChangedOperations();
            for (Entry<PathItem.HttpMethod, ChangedOperation> entry : changedOperations
                    .entrySet()) {
                String method = entry.getKey().toString();
                ChangedOperation changedOperation = entry.getValue();
                String desc = changedOperation.getSummary();

                StringBuffer ul_detail = new StringBuffer();
                if (changedOperation.isDiffParam()) {
                    ul_detail.append(PRE_LI).append("Parameter")
                            .append(ul_param(changedOperation.getChangedParameters()));
                }
                if (changedOperation.isDiffRequest()) {
                    ul_detail.append(PRE_LI).append("Request")
                            .append(ul_request(changedOperation.getRequestChangedContent()));
                }
                if (changedOperation.isDiffResponse()) {
                    ul_detail.append(PRE_LI).append("Return Type")
                            .append(ul_response(changedOperation.getChangedApiResponse()));
                }
                sb.append(LI).append(CODE).append(method).append(CODE)
                        .append(" " + pathUrl).append(" " + desc + "  \n")
                        .append(ul_detail);
            }
        }
        return sb.toString();
    }

    private String ul_response(ChangedApiResponse changedApiResponse) {
        Map<String, ApiResponse> addResponses = changedApiResponse.getAddResponses();
        Map<String, ApiResponse> delResponses = changedApiResponse.getMissingResponses();
        Map<String, ChangedResponse> changedResponses = changedApiResponse.getChangedResponses();
        StringBuffer sb = new StringBuffer("\n\n");
        for (String propName : addResponses.keySet()) {
            sb.append(PRE_LI).append(PRE_CODE).append(li_addResponse(propName, addResponses.get(propName))).append("\n");
        }
        for (String propName : delResponses.keySet()) {
            sb.append(PRE_LI).append(PRE_CODE).append(li_missingResponse(propName, delResponses.get(propName))).append("\n");
        }
        for (String propName : changedResponses.keySet()) {
            sb.append(PRE_LI).append(PRE_CODE).append(li_changedResponse(propName, changedResponses.get(propName))).append("\n");
        }
        return sb.toString();
    }

    private String li_addResponse(String name, ApiResponse response) {
        StringBuffer sb = new StringBuffer("");
        sb.append(String.format("New response : [%s]", name)).append(null == response.getDescription() ? "" : (" //" + response.getDescription()));
        return sb.toString();
    }

    private String li_missingResponse(String name, ApiResponse response) {
        StringBuffer sb = new StringBuffer("");
        sb.append(String.format("Deleted response : [%s]", name)).append(null == response.getDescription() ? "" : (" //" + response.getDescription()));
        return sb.toString();
    }

    private String li_changedResponse(String name, ChangedResponse response) {
        StringBuffer sb = new StringBuffer("");
        sb.append(String.format("Changed response : [%s]", name)).append(null == response.getDescription() ? "" : (" //" + response.getDescription()));
        return sb.toString();
    }

    private String ul_request(ChangedContent changedContent) {
        StringBuffer sb = new StringBuffer("\n\n");
        for (String propName : changedContent.getIncreased().keySet()) {
            sb.append(PRE_LI).append(PRE_CODE).append(li_addRequest(propName, changedContent.getIncreased().get(propName))).append("\n");
        }
        for (String propName : changedContent.getMissing().keySet()) {
            sb.append(PRE_LI).append(PRE_CODE).append(li_missingRequest(propName, changedContent.getMissing().get(propName))).append("\n");
        }
        for (String propName : changedContent.getChanged().keySet()) {
            sb.append(PRE_LI).append(PRE_CODE).append(li_changedRequest(propName, changedContent.getChanged().get(propName))).append("\n");
        }
        return sb.toString();
    }

    private String li_addRequest(String name, MediaType request) {
        StringBuffer sb = new StringBuffer("");
        sb.append(String.format("New request body : '%s'", name));
        return sb.toString();
    }

    private String li_missingRequest(String name, MediaType request) {
        StringBuffer sb = new StringBuffer("");
        sb.append(String.format("Deleted request body : [%s]", name));
        return sb.toString();
    }

    private String li_changedRequest(String name, ChangedMediaType request) {
        StringBuffer sb = new StringBuffer("");
        sb.append(String.format("Changed response : [%s]", name));
        return sb.toString();
    }

    private String ul_param(ChangedParameters changedParameters) {
        List<Parameter> addParameters = changedParameters.getIncreased();
        List<Parameter> delParameters = changedParameters.getMissing();
        List<ChangedParameter> changed = changedParameters.getChanged();
        StringBuffer sb = new StringBuffer("\n\n");
        for (Parameter param : addParameters) {
            sb.append(PRE_LI).append(PRE_CODE)
                    .append(li_addParam(param) + "\n");
        }
        for (ChangedParameter param : changed) {
            boolean changeRequired = param.isChangeRequired();
            boolean changeDescription = param.isChangeDescription();
            if (changeRequired || changeDescription) sb.append(PRE_LI)
                    .append(PRE_CODE).append(li_changedParam(param) + "\n");
        }
        for (Parameter param : delParameters) {
            sb.append(PRE_LI).append(PRE_CODE)
                    .append(li_missingParam(param) + "\n");
        }
        return sb.toString();
    }

    private String li_addParam(Parameter param) {
        StringBuffer sb = new StringBuffer("");
        sb.append("Add ").append(param.getName())
                .append(null == param.getDescription() ? ""
                        : (" //" + param.getDescription()));
        return sb.toString();
    }

    private String li_missingParam(Parameter param) {
        StringBuffer sb = new StringBuffer("");
        sb.append("Delete ").append(param.getName())
                .append(null == param.getDescription() ? ""
                        : (" //" + param.getDescription()));
        return sb.toString();
    }

    private String li_changedParam(ChangedParameter changeParam) {
        boolean changeRequired = changeParam.isChangeRequired();
        boolean changeDescription = changeParam.isChangeDescription();
        Parameter rightParam = changeParam.getRightParameter();
        Parameter leftParam = changeParam.getLeftParameter();
        StringBuffer sb = new StringBuffer("");
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

}
