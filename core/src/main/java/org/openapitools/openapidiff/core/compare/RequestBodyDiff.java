package org.openapitools.openapidiff.core.compare;

import static java.util.Optional.ofNullable;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.parameters.RequestBody;
import java.util.HashSet;
import java.util.Map;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedRequestBody;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredBuilder;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;
import org.openapitools.openapidiff.core.utils.RefPointer;
import org.openapitools.openapidiff.core.utils.RefType;

/** Created by adarsh.sharma on 28/12/17. */
public class RequestBodyDiff extends ReferenceDiffCache<RequestBody, ChangedRequestBody> {
  private static final RefPointer<RequestBody> refPointer =
      new RefPointer<>(RefType.REQUEST_BODIES);
  private final OpenApiDiff openApiDiff;

  public RequestBodyDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  private static Map<String, Object> getExtensions(RequestBody body) {
    return ofNullable(body).map(RequestBody::getExtensions).orElse(null);
  }

  public DeferredChanged<ChangedRequestBody> diff(
      RequestBody left, RequestBody right, DiffContext context) {
    String leftRef = left != null ? left.get$ref() : null,
        rightRef = right != null ? right.get$ref() : null;
    return cachedDiff(new HashSet<>(), left, right, leftRef, rightRef, context);
  }

  @Override
  protected DeferredChanged<ChangedRequestBody> computeDiff(
      HashSet<String> refSet, RequestBody left, RequestBody right, DiffContext context) {
    DeferredBuilder<Changed> builder = new DeferredBuilder<Changed>();

    Content oldRequestContent = new Content();
    Content newRequestContent = new Content();
    RequestBody oldRequestBody = null;
    RequestBody newRequestBody = null;
    if (left != null) {
      oldRequestBody =
          refPointer.resolveRef(
              openApiDiff.getOldSpecOpenApi().getComponents(), left, left.get$ref());
      if (oldRequestBody.getContent() != null) {
        oldRequestContent = oldRequestBody.getContent();
      }
    }
    if (right != null) {
      newRequestBody =
          refPointer.resolveRef(
              openApiDiff.getNewSpecOpenApi().getComponents(), right, right.get$ref());
      if (newRequestBody.getContent() != null) {
        newRequestContent = newRequestBody.getContent();
      }
    }
    boolean leftRequired =
        oldRequestBody != null && Boolean.TRUE.equals(oldRequestBody.getRequired());
    boolean rightRequired =
        newRequestBody != null && Boolean.TRUE.equals(newRequestBody.getRequired());

    ChangedRequestBody changedRequestBody =
        new ChangedRequestBody(oldRequestBody, newRequestBody, context)
            .setChangeRequired(leftRequired != rightRequired);
    builder
        .with(
            openApiDiff
                .getMetadataDiff()
                .diff(
                    oldRequestBody != null ? oldRequestBody.getDescription() : null,
                    newRequestBody != null ? newRequestBody.getDescription() : null,
                    context))
        .ifPresent(changedRequestBody::setDescription);
    builder
        .with(openApiDiff.getContentDiff().diff(oldRequestContent, newRequestContent, context))
        .ifPresent(changedRequestBody::setContent);
    builder
        .with(
            openApiDiff
                .getExtensionsDiff()
                .diff(getExtensions(left), getExtensions(right), context))
        .ifPresent(changedRequestBody::setExtensions);

    return builder.buildIsChanged(changedRequestBody);
  }
}
