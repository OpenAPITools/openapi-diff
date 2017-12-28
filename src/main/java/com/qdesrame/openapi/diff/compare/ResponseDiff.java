package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedResponse;
import io.swagger.v3.oas.models.responses.ApiResponse;

import java.util.Objects;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
public class ResponseDiff {
    private OpenApiDiff openApiDiff;

    public ResponseDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
    }

    public ChangedResponse diff(ApiResponse left, ApiResponse right) {
        ChangedResponse changedResponse = new ChangedResponse(left, right);

        changedResponse.setChangedContent(openApiDiff.getContentDiff().diff(left.getContent(), right.getContent()));
        changedResponse.setChangedHeaders(openApiDiff.getHeadersDiff().diff(left.getHeaders(), right.getHeaders()));
        changedResponse.setChangeDescription(!Objects.equals(left.getDescription(), right.getDescription()));

        return changedResponse;
    }
}
