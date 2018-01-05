package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedResponse;
import com.qdesrame.openapi.diff.utils.RefPointer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.responses.ApiResponse;

import java.util.Objects;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
public class ResponseDiff {
    private OpenApiDiff openApiDiff;
    private Components leftComponents;
    private Components rightComponents;
    private ReferenceDiffCache<ChangedResponse> responseReferenceDiffCache;

    public ResponseDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.leftComponents = openApiDiff.getOldSpecOpenApi() != null ? openApiDiff.getOldSpecOpenApi().getComponents() : null;
        this.rightComponents = openApiDiff.getNewSpecOpenApi() != null ? openApiDiff.getNewSpecOpenApi().getComponents() : null;
        this.responseReferenceDiffCache = new ReferenceDiffCache<>();
    }

    public ChangedResponse diff(ApiResponse left, ApiResponse right) {
        String leftRef = left.get$ref();
        String rightRef = right.get$ref();
        boolean areBothRefResponses = leftRef != null && rightRef != null;
        if (areBothRefResponses) {
            ChangedResponse changedResponseFromCache = responseReferenceDiffCache.getFromCache(leftRef, rightRef);
            if (changedResponseFromCache != null) {
                return changedResponseFromCache;
            }
        }

        left = RefPointer.Replace.response(leftComponents, left);
        right = RefPointer.Replace.response(rightComponents, right);

        ChangedResponse changedResponse = new ChangedResponse(left, right);

        changedResponse.setChangedContent(openApiDiff.getContentDiff().diff(left.getContent(), right.getContent()));
        changedResponse.setChangedHeaders(openApiDiff.getHeadersDiff().diff(left.getHeaders(), right.getHeaders()));
        changedResponse.setChangeDescription(!Objects.equals(left.getDescription(), right.getDescription()));

        if (areBothRefResponses) {
            responseReferenceDiffCache.addToCache(leftRef, rightRef, changedResponse);
        }

        return changedResponse.isDiff() ? changedResponse : null;
    }
}
