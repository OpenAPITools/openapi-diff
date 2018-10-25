package com.qdesrame.openapi.diff.compare;

import static com.qdesrame.openapi.diff.utils.ChangedUtils.isChanged;

import com.qdesrame.openapi.diff.model.ChangedPath;
import com.qdesrame.openapi.diff.model.DiffContext;
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

  public Optional<ChangedPath> diff(PathItem left, PathItem right, DiffContext context) {
    ChangedPath changedPath = new ChangedPath(context.getUrl(), left, right, context);

    Map<PathItem.HttpMethod, Operation> oldOperationMap = left.readOperationsMap();
    Map<PathItem.HttpMethod, Operation> newOperationMap = right.readOperationsMap();
    MapKeyDiff<PathItem.HttpMethod, Operation> operationsDiff =
        MapKeyDiff.diff(oldOperationMap, newOperationMap);
    changedPath.setIncreased(operationsDiff.getIncreased());
    changedPath.setMissing(operationsDiff.getMissing());
    List<PathItem.HttpMethod> sharedMethods = operationsDiff.getSharedKey();
    for (PathItem.HttpMethod method : sharedMethods) {
      Operation oldOperation = oldOperationMap.get(method);
      Operation newOperation = newOperationMap.get(method);
      openApiDiff
          .getOperationDiff()
          .diff(oldOperation, newOperation, context.copyWithMethod(method))
          .ifPresent(changedPath.getChanged()::add);
    }
    openApiDiff
        .getExtensionsDiff()
        .diff(left.getExtensions(), right.getExtensions(), context)
        .ifPresent(changedPath::setChangedExtensions);
    return isChanged(changedPath);
  }
}
