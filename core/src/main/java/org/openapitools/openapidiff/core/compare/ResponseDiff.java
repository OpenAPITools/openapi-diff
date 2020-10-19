package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.HashSet;
import java.util.Optional;
import org.openapitools.openapidiff.core.model.ChangedResponse;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.utils.RefPointer;
import org.openapitools.openapidiff.core.utils.RefType;

/** Created by adarsh.sharma on 28/12/17. */
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

  public Optional<ChangedResponse> diff(ApiResponse left, ApiResponse right, DiffContext context) {
    return cachedDiff(new HashSet<>(), left, right, left.get$ref(), right.get$ref(), context);
  }

  @Override
  protected Optional<ChangedResponse> computeDiff(
      HashSet<String> refSet, ApiResponse left, ApiResponse right, DiffContext context) {
    left = refPointer.resolveRef(leftComponents, left, left.get$ref());
    right = refPointer.resolveRef(rightComponents, right, right.get$ref());

    ChangedResponse changedResponse = new ChangedResponse(left, right, context);
    openApiDiff
        .getMetadataDiff()
        .diff(left.getDescription(), right.getDescription(), context)
        .ifPresent(changedResponse::setDescription);
    openApiDiff
        .getContentDiff()
        .diff(left.getContent(), right.getContent(), context)
        .ifPresent(changedResponse::setContent);
    openApiDiff
        .getHeadersDiff()
        .diff(left.getHeaders(), right.getHeaders(), context)
        .ifPresent(changedResponse::setHeaders);
    openApiDiff
        .getExtensionsDiff()
        .diff(left.getExtensions(), right.getExtensions(), context)
        .ifPresent(changedResponse::setExtensions);
    return isChanged(changedResponse);
  }
}
