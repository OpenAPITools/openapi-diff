package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedOperation;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

import java.util.Optional;

/**
 * Created by adarsh.sharma on 04/01/18.
 */
public class OperationDiff {
    private OpenApiDiff openApiDiff;

    public OperationDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
    }

    public Optional<ChangedOperation> diff(String pathUrl, PathItem.HttpMethod method, Operation oldOperation, Operation newOperation) {
        ChangedOperation changedOperation = new ChangedOperation(pathUrl, method, oldOperation, newOperation);

        changedOperation.setSummary(newOperation.getSummary());
        changedOperation.setDeprecated(!Boolean.TRUE.equals(oldOperation.getDeprecated()) && Boolean.TRUE.equals(newOperation.getDeprecated()));

        if (oldOperation.getRequestBody() != null || newOperation.getRequestBody() != null) {
            openApiDiff.getRequestBodyDiff().diff(oldOperation.getRequestBody(), newOperation.getRequestBody())
                    .ifPresent(changedOperation::setChangedRequestBody);
        }

        openApiDiff.getParametersDiff().diff(oldOperation.getParameters(), newOperation.getParameters())
                .ifPresent(changedOperation::setChangedParameters);

        if (oldOperation.getResponses() != null || newOperation.getResponses() != null) {
            openApiDiff.getApiResponseDiff().diff(oldOperation.getResponses(), newOperation.getResponses())
                    .ifPresent(changedOperation::setChangedApiResponse);
        }

        return changedOperation.isDiff() ? Optional.of(changedOperation) : Optional.empty();
    }
}
