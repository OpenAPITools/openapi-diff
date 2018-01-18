package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedPath;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PathDiff {
    private OpenApiDiff openApiDiff;

    public PathDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
    }

    public Optional<ChangedPath> diff(String pathUrl, Map<String, String> pathParameters, PathItem left, PathItem right) {
        ChangedPath changedPath = new ChangedPath(pathUrl, left, right);

        Map<PathItem.HttpMethod, Operation> oldOperationMap = left.readOperationsMap();
        Map<PathItem.HttpMethod, Operation> newOperationMap = right.readOperationsMap();
        MapKeyDiff<PathItem.HttpMethod, Operation> operationsDiff = MapKeyDiff.diff(oldOperationMap,
                newOperationMap);
        changedPath.setIncreased(operationsDiff.getIncreased());
        changedPath.setMissing(operationsDiff.getMissing());
        List<PathItem.HttpMethod> sharedMethods = operationsDiff.getSharedKey();
        for (PathItem.HttpMethod method : sharedMethods) {
            Operation oldOperation = oldOperationMap.get(method);
            Operation newOperation = newOperationMap.get(method);
            openApiDiff.getOperationDiff().diff(pathUrl, method, pathParameters, oldOperation, newOperation).ifPresent(changedPath.getChanged()::add);
        }
        return changedPath.isDiff() ? Optional.of(changedPath) : Optional.empty();
    }
}
