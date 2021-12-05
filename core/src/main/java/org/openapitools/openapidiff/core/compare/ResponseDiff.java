package org.openapitools.openapidiff.core.compare;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.HashSet;
import java.util.Optional;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedResponse;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredBuilder;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;
import org.openapitools.openapidiff.core.model.deferred.RealizedChanged;
import org.openapitools.openapidiff.core.utils.RefPointer;
import org.openapitools.openapidiff.core.utils.RefType;

public class ResponseDiff extends ReferenceDiffCache<ApiResponse, ChangedResponse> {
  private static final RefPointer<ApiResponse> refPointer = new RefPointer<>(RefType.RESPONSES);
  private final OpenApiDiff openApiDiff;
  private final Components leftComponents;
  private final Components rightComponents;

  public ResponseDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
    this.leftComponents =
        openApiDiff.getOldSpecOpenApi() != null
            ? openApiDiff.getOldSpecOpenApi().getComponents()
            : null;
    this.rightComponents =
        openApiDiff.getNewSpecOpenApi() != null
            ? openApiDiff.getNewSpecOpenApi().getComponents()
            : null;
  }

  public DeferredChanged<ChangedResponse> diff(
      ApiResponse left, ApiResponse right, DiffContext context) {
    if (left == null && right == null) {
      return new RealizedChanged(Optional.empty());
    }
    if ((left == null && right != null) || (left != null && right == null)) {
      return new RealizedChanged(Optional.of(new ChangedResponse(left, right, context)));
    }
    return cachedDiff(new HashSet<>(), left, right, left.get$ref(), right.get$ref(), context);
  }

  @Override
  protected DeferredChanged<ChangedResponse> computeDiff(
      HashSet<String> refSet, ApiResponse left, ApiResponse right, DiffContext context) {
    left = refPointer.resolveRef(leftComponents, left, left.get$ref());
    right = refPointer.resolveRef(rightComponents, right, right.get$ref());

    DeferredBuilder<Changed> builder = new DeferredBuilder<>();
    ChangedResponse changedResponse = new ChangedResponse(left, right, context);
    builder
        .with(
            openApiDiff
                .getMetadataDiff()
                .diff(left.getDescription(), right.getDescription(), context))
        .ifPresent(changedResponse::setDescription);
    builder
        .with(openApiDiff.getContentDiff().diff(left.getContent(), right.getContent(), context))
        .ifPresent(changedResponse::setContent);
    builder
        .with(openApiDiff.getHeadersDiff().diff(left.getHeaders(), right.getHeaders(), context))
        .ifPresent(changedResponse::setHeaders);
    builder
        .with(
            openApiDiff
                .getExtensionsDiff()
                .diff(left.getExtensions(), right.getExtensions(), context))
        .ifPresent(changedResponse::setExtensions);

    return builder.buildIsChanged(changedResponse);
  }
}
