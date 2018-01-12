package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
    private ChangedSecurityRequirements changedSecurityRequirements;

    public ChangedOperation(String pathUrl, PathItem.HttpMethod httpMethod, Operation oldOperation, Operation newOperation) {
        this.httpMethod = httpMethod;
        this.pathUrl = pathUrl;
        this.oldOperation = oldOperation;
        this.newOperation = newOperation;
    }

    @Override
    public boolean isDiff() {
        return deprecated || isDiffParam() || isDiffRequest() || isDiffResponse() || isDiffSecurity();
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return (changedParameters == null || changedParameters.isDiffBackwardCompatible())
                && (changedRequestBody == null || changedRequestBody.isDiffBackwardCompatible())
                && (changedApiResponse == null || changedApiResponse.isDiffBackwardCompatible())
                && (changedSecurityRequirements == null || changedSecurityRequirements.isDiffBackwardCompatible());
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

    public boolean isDiffSecurity() {
        return changedSecurityRequirements != null && changedSecurityRequirements.isDiff();
    }
}
