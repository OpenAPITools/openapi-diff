package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedRequestBody;
import com.qdesrame.openapi.diff.utils.RefPointer;
import com.qdesrame.openapi.diff.utils.RefType;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.parameters.RequestBody;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
public class RequestBodyDiff extends ReferenceDiffCache<RequestBody, ChangedRequestBody> {
    private OpenApiDiff openApiDiff;
    private static RefPointer<RequestBody> refPointer = new RefPointer<>(RefType.REQUEST_BODIES);

    public RequestBodyDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
    }

    public Optional<ChangedRequestBody> diff(RequestBody left, RequestBody right) {
        return super.cachedDiff(left, right, left.get$ref(), right.get$ref());
    }

    @Override
    protected Optional<ChangedRequestBody> computeDiff(RequestBody left, RequestBody right) {
        Content oldRequestContent = new Content();
        Content newRequestContent = new Content();
        RequestBody oldRequestBody = null;
        RequestBody newRequestBody = null;
        if (left != null) {
            oldRequestBody = refPointer.resolveRef(openApiDiff.getOldSpecOpenApi().getComponents(), left, left.get$ref());
            if (oldRequestBody.getContent() != null) {
                oldRequestContent = oldRequestBody.getContent();
            }
        }
        if (right != null) {
            newRequestBody = refPointer.resolveRef(openApiDiff.getNewSpecOpenApi().getComponents(), right, right.get$ref());
            if (newRequestBody.getContent() != null) {
                newRequestContent = newRequestBody.getContent();
            }
        }

        ChangedRequestBody changedRequestBody = new ChangedRequestBody(oldRequestBody, newRequestBody);

        boolean leftRequired = oldRequestBody != null && Boolean.TRUE.equals(oldRequestBody.getRequired());
        boolean rightRequired = newRequestBody != null && Boolean.TRUE.equals(newRequestBody.getRequired());
        changedRequestBody.setChangeRequired(leftRequired != rightRequired);

        String leftDescription = oldRequestBody != null ? oldRequestBody.getDescription() : null;
        String rightDescription = newRequestBody != null ? newRequestBody.getDescription() : null;
        changedRequestBody.setChangeDescription(!Objects.equals(leftDescription, rightDescription));

        openApiDiff.getContentDiff().diff(oldRequestContent, newRequestContent).ifPresent(changedRequestBody::setChangedContent);

        return changedRequestBody.isDiff() ? Optional.of(changedRequestBody) : Optional.empty();
    }
}
