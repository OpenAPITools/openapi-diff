package com.qdesrame.openapi.diff.model;

import io.swagger.oas.models.Operation;

public class ChangedOperation implements Changed {

    private Operation oldOperation;
    private Operation newOperation;
    private String summary;
    private boolean deprecated;
    private ChangedParameters changedParameters;
    private ChangedContent requestChangedContent;
    private ChangedApiResponse changedApiResponse;

    public ChangedOperation(Operation oldOperation, Operation newOperation) {
        this.oldOperation = oldOperation;
        this.newOperation = newOperation;
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

    public ChangedContent getRequestChangedContent() {
        return requestChangedContent;
    }

    public void setRequestChangedContent(ChangedContent requestChangedContent) {
        this.requestChangedContent = requestChangedContent;
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
        return changedParameters.isDiffBackwardCompatible()
                && requestChangedContent.isDiffBackwardCompatible(true)
                && changedApiResponse.isDiffBackwardCompatible();
    }

    public boolean isDiffParam() {
        return changedParameters.isDiff();
    }

    public boolean isDiffResponse() {
        return changedApiResponse.isDiff();
    }

    public boolean isDiffRequest() {
        return requestChangedContent.isDiff();
    }

}
