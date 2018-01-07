package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedResponse;
import com.qdesrame.openapi.diff.utils.RefPointer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.responses.ApiResponse;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
public class ResponseDiff extends ReferenceDiffCache<ApiResponse, ChangedResponse> {
    private OpenApiDiff openApiDiff;
    private Components leftComponents;
    private Components rightComponents;

    public ResponseDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.leftComponents = openApiDiff.getOldSpecOpenApi() != null ? openApiDiff.getOldSpecOpenApi().getComponents() : null;
        this.rightComponents = openApiDiff.getNewSpecOpenApi() != null ? openApiDiff.getNewSpecOpenApi().getComponents() : null;
    }

    public Optional<ChangedResponse> diff(ApiResponse left, ApiResponse right) {
        return super.cachedDiff(left, right, left.get$ref(), right.get$ref());
    }

    @Override
    protected Optional<ChangedResponse> computeDiff(ApiResponse left, ApiResponse right) {
        left = RefPointer.Replace.response(leftComponents, left);
        right = RefPointer.Replace.response(rightComponents, right);

        ChangedResponse changedResponse = new ChangedResponse(left, right);

        openApiDiff.getContentDiff().diff(left.getContent(), right.getContent()).ifPresent(changedResponse::setChangedContent);
        openApiDiff.getHeadersDiff().diff(left.getHeaders(), right.getHeaders()).ifPresent(changedResponse::setChangedHeaders);
        changedResponse.setChangeDescription(!Objects.equals(left.getDescription(), right.getDescription()));

        return changedResponse.isDiff() ? Optional.of(changedResponse) : Optional.empty();    }
}
