package org.openapitools.openapidiff.core.compare;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import java.util.List;
import java.util.Map;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedPath;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredBuilder;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;

public class PathDiff {
  private final OpenApiDiff openApiDiff;

  public PathDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  public DeferredChanged<ChangedPath> diff(PathItem left, PathItem right, DiffContext context) {
    Map<PathItem.HttpMethod, Operation> oldOperationMap = left.readOperationsMap();
    Map<PathItem.HttpMethod, Operation> newOperationMap = right.readOperationsMap();
    MapKeyDiff<PathItem.HttpMethod, Operation> operationsDiff =
        MapKeyDiff.diff(oldOperationMap, newOperationMap);
    List<PathItem.HttpMethod> sharedMethods = operationsDiff.getSharedKey();
    DeferredBuilder<Changed> builder = new DeferredBuilder<>();

    ChangedPath changedPath =
        new ChangedPath(context.getUrl(), left, right, context)
            .setIncreased(operationsDiff.getIncreased())
            .setMissing(operationsDiff.getMissing());
    for (PathItem.HttpMethod method : sharedMethods) {
      Operation oldOperation = oldOperationMap.get(method);
      Operation newOperation = newOperationMap.get(method);
      builder
          .with(
              openApiDiff
                  .getOperationDiff()
                  .diff(oldOperation, newOperation,
                          context.copyWithMethod(method).copyWithLeftRightUrls(
                                  context.getLeftUrl(),
                                  context.getRightUrl()
                          )
                  )
          ).ifPresent(changedPath.getChanged()::add);
    }
    builder
        .with(
            openApiDiff
                .getExtensionsDiff()
                .diff(left.getExtensions(), right.getExtensions(), context))
        .ifPresent(changedPath::setExtensions);

    return builder.buildIsChanged(changedPath);
  }
}
