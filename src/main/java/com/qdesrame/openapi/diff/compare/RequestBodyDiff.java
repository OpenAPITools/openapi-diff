package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedRequestBody;
import com.qdesrame.openapi.diff.utils.RefPointer;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.parameters.RequestBody;

import java.util.Objects;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
public class RequestBodyDiff {
    private OpenApiDiff openApiDiff;

    public RequestBodyDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
    }

    public ChangedRequestBody diff(RequestBody left, RequestBody right) {
        Content oldRequestContent = new Content();
        Content newRequestContent = new Content();
        RequestBody oldRequestBody = null;
        RequestBody newRequestBody = null;
        if (left != null) {
            oldRequestBody = RefPointer.Replace.requestBody(openApiDiff.getOldSpecOpenApi().getComponents(), left);
            if (oldRequestBody.getContent() != null) {
                oldRequestContent = oldRequestBody.getContent();
            }
        }
        if (right != null) {
            newRequestBody = RefPointer.Replace.requestBody(openApiDiff.getOldSpecOpenApi().getComponents(), right);
            if (newRequestBody.getContent() != null) {
                newRequestContent = newRequestBody.getContent();
            }
        }

        ChangedRequestBody changedRequestBody = new ChangedRequestBody(left, right);

        boolean leftRequired = oldRequestBody != null ? Boolean.TRUE.equals(oldRequestBody.getRequired()) : false;
        boolean rightRequired = newRequestBody != null ? Boolean.TRUE.equals(newRequestBody.getRequired()) : false;
        changedRequestBody.setChangeRequired(leftRequired != rightRequired);

        String leftDescription = oldRequestBody != null ? oldRequestBody.getDescription() : null;
        String rightDescription = newRequestBody != null ? newRequestBody.getDescription() : null;
        changedRequestBody.setChangeDescription(!Objects.equals(leftDescription, rightDescription));

        changedRequestBody.setChangedContent(openApiDiff.getContentDiff().diff(oldRequestContent, newRequestContent));
        return changedRequestBody;
    }
}
