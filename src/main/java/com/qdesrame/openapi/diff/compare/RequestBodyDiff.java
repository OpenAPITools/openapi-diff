package com.qdesrame.openapi.diff.compare;

import static com.qdesrame.openapi.diff.utils.ChangedUtils.isChanged;
import static java.util.Optional.ofNullable;

import com.qdesrame.openapi.diff.model.ChangedRequestBody;
import com.qdesrame.openapi.diff.model.DiffContext;
import com.qdesrame.openapi.diff.utils.RefPointer;
import com.qdesrame.openapi.diff.utils.RefType;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.parameters.RequestBody;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Created by adarsh.sharma on 28/12/17. */
public class RequestBodyDiff extends ReferenceDiffCache<RequestBody, ChangedRequestBody> {
  private OpenApiDiff openApiDiff;
  private static RefPointer<RequestBody> refPointer = new RefPointer<>(RefType.REQUEST_BODIES);

  public RequestBodyDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  public Optional<ChangedRequestBody> diff(
      RequestBody left, RequestBody right, DiffContext context) {
    String leftRef = left != null ? left.get$ref() : null,
        rightRef = right != null ? right.get$ref() : null;
    return cachedDiff(new HashSet<>(), left, right, leftRef, rightRef, context);
  }

  private static Map<String, Object> getExtensions(RequestBody body) {
    return ofNullable(body).map(RequestBody::getExtensions).orElse(null);
  }

  @Override
  protected Optional<ChangedRequestBody> computeDiff(
      HashSet<String> refSet, RequestBody left, RequestBody right, DiffContext context) {
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
    ChangedRequestBody changedRequestBody =
        new ChangedRequestBody(oldRequestBody, newRequestBody, context);

    boolean leftRequired =
        oldRequestBody != null && Boolean.TRUE.equals(oldRequestBody.getRequired());
    boolean rightRequired =
        newRequestBody != null && Boolean.TRUE.equals(newRequestBody.getRequired());
    changedRequestBody.setChangeRequired(leftRequired != rightRequired);

    String leftDescription = oldRequestBody != null ? oldRequestBody.getDescription() : null;
    String rightDescription = newRequestBody != null ? newRequestBody.getDescription() : null;
    changedRequestBody.setChangeDescription(!Objects.equals(leftDescription, rightDescription));

    openApiDiff
        .getContentDiff()
        .diff(oldRequestContent, newRequestContent, context)
        .ifPresent(changedRequestBody::setChangedContent);
    openApiDiff
        .getExtensionsDiff()
        .diff(getExtensions(left), getExtensions(right), context)
        .ifPresent(changedRequestBody::setChangedExtensions);

    return isChanged(changedRequestBody);
  }
}
