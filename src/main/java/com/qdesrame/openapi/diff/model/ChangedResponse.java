package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
