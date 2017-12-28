package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.responses.ApiResponse;

public class ChangedResponse implements Changed {
    private ApiResponse oldApiResponse;
    private ApiResponse newApiResponse;

    private boolean changeDescription;
    private ChangedHeaders changedHeaders;
    private ChangedContent changedContent;

    public ChangedResponse(ApiResponse oldApiResponse, ApiResponse newApiResponse) {
        this.oldApiResponse = oldApiResponse;
        this.newApiResponse = newApiResponse;
    }

    public boolean isChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(boolean changeDescription) {
        this.changeDescription = changeDescription;
    }

    public ApiResponse getOldApiResponse() {
        return oldApiResponse;
    }

    public ApiResponse getNewApiResponse() {
        return newApiResponse;
    }

    public ChangedContent getChangedContent() {
        return changedContent;
    }

    public void setChangedContent(ChangedContent changedContent) {
        this.changedContent = changedContent;
    }

    public ChangedHeaders getChangedHeaders() {
        return changedHeaders;
    }

    public void setChangedHeaders(ChangedHeaders changedHeaders) {
        this.changedHeaders = changedHeaders;
    }

    @Override
    public boolean isDiff() {
        return changeDescription
                ||(changedContent != null && changedContent.isDiff())
                || (changedHeaders != null && changedHeaders.isDiff());
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return (changedContent == null || changedContent.isDiffBackwardCompatible(false))
                && (changedHeaders == null || changedHeaders.isDiffBackwardCompatible());
    }
}
