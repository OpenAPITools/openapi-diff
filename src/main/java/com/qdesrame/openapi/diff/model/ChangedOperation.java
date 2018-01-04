package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;


public class ChangedOperation implements Changed {

    private String pathUrl;
    private PathItem.HttpMethod httpMethod;
    private Operation oldOperation;
    private Operation newOperation;
    private String summary;
    private boolean deprecated;
    private ChangedParameters changedParameters;
    private ChangedRequestBody changedRequestBody;
    private ChangedApiResponse changedApiResponse;

    public ChangedOperation(String pathUrl, PathItem.HttpMethod httpMethod, Operation oldOperation, Operation newOperation) {
        this.httpMethod = httpMethod;
        this.pathUrl = pathUrl;
        this.oldOperation = oldOperation;
        this.newOperation = newOperation;
    }

    public String getPathUrl() {
        return pathUrl;
    }

    public PathItem.HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Operation getOldOperation() {
        return oldOperation;
    }

    public Operation getNewOperation() {
        return newOperation;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public ChangedParameters getChangedParameters() {
        return changedParameters;
    }

    public void setChangedParameters(ChangedParameters changedParameters) {
        this.changedParameters = changedParameters;
    }

    public ChangedRequestBody getChangedRequestBody() {
        return changedRequestBody;
    }

    public void setChangedRequestBody(ChangedRequestBody changedRequestBody) {
        this.changedRequestBody = changedRequestBody;
    }

    public ChangedApiResponse getChangedApiResponse() {
        return changedApiResponse;
    }

    public void setChangedApiResponse(ChangedApiResponse changedApiResponse) {
        this.changedApiResponse = changedApiResponse;
    }

    @Override
    public boolean isDiff() {
        return deprecated || isDiffParam() || isDiffRequest() || isDiffResponse();
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return (changedParameters == null || changedParameters.isDiffBackwardCompatible())
                && (changedRequestBody == null || changedRequestBody.isDiffBackwardCompatible())
                && (changedApiResponse == null || changedApiResponse.isDiffBackwardCompatible());
    }

    public boolean isDiffParam() {
        return changedParameters != null && changedParameters.isDiff();
    }

    public boolean isDiffResponse() {
        return changedApiResponse != null && changedApiResponse.isDiff();
    }

    public boolean isDiffRequest() {
        return changedRequestBody != null && changedRequestBody.isDiff();
    }

}
