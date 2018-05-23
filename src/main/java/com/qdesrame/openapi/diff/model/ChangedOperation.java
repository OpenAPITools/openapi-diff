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
    public DiffResult isChanged() {
        //TODO BETTER HANDLING FOR DEPRECIATION
        if (!deprecated && isChangedParam().isUnchanged() && isChangedRequest().isUnchanged()
                && isChangedResponse().isUnchanged() && isChangedSecurity().isUnchanged()) {
            return DiffResult.NO_CHANGES;
        }
        if (isChangedParam().isCompatible() && isChangedRequest().isCompatible()
                && isChangedResponse().isCompatible() && isChangedSecurity().isCompatible()) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }

    public DiffResult isChangedParam() {
        return changedParameters == null ? DiffResult.NO_CHANGES : changedParameters.isChanged();
    }

    public DiffResult isChangedResponse() {
        return changedApiResponse == null ? DiffResult.NO_CHANGES : changedApiResponse.isChanged();
    }

    public DiffResult isChangedRequest() {
        return changedRequestBody == null ? DiffResult.NO_CHANGES : changedRequestBody.isChanged();
    }

    public DiffResult isChangedSecurity() {
        return changedSecurityRequirements == null ? DiffResult.NO_CHANGES : changedSecurityRequirements.isChanged();
    }
}
