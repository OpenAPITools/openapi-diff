package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedApiResponse;
import com.qdesrame.openapi.diff.model.ChangedOperation;
import com.qdesrame.openapi.diff.model.ChangedParameters;
import com.qdesrame.openapi.diff.model.ChangedRequestBody;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

/**
 * Created by adarsh.sharma on 04/01/18.
 */
public class OperationDiff {
    private OpenApiDiff openApiDiff;

    public OperationDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
    }

    public ChangedOperation diff(String pathUrl, PathItem.HttpMethod method, Operation oldOperation, Operation newOperation) {
        ChangedOperation changedOperation = new ChangedOperation(pathUrl, method, oldOperation, newOperation);

        changedOperation.setSummary(newOperation.getSummary());
        changedOperation.setDeprecated(!Boolean.TRUE.equals(oldOperation.getDeprecated()) && Boolean.TRUE.equals(newOperation.getDeprecated()));

        if (oldOperation.getRequestBody() != null || newOperation.getRequestBody() != null) {
            ChangedRequestBody changedRequestBody = openApiDiff.getRequestBodyDiff().diff(oldOperation.getRequestBody(), newOperation.getRequestBody());
            changedOperation.setChangedRequestBody(changedRequestBody);
        }

        ChangedParameters changedParameters = openApiDiff.getParametersDiff().diff(oldOperation.getParameters(), newOperation.getParameters());
        changedOperation.setChangedParameters(changedParameters);

        if (oldOperation.getResponses() != null || newOperation.getResponses() != null) {
            ChangedApiResponse changedApiResponse = openApiDiff.getApiResponseDiff().diff(oldOperation.getResponses(), newOperation.getResponses());
            changedOperation.setChangedApiResponse(changedApiResponse);
        }

        return changedOperation.isDiff() ? changedOperation : null;
    }
}
