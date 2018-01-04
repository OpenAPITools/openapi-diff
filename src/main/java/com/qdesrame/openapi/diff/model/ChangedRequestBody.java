package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.parameters.RequestBody;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by adarsh.sharma on 27/12/17.
 */
@Getter
@Setter
public class ChangedRequestBody implements Changed {
    private RequestBody oldRequestBody;
    private RequestBody newRequestBody;
    private boolean changeDescription;
    private boolean changeRequired;
    private ChangedContent changedContent;

    public ChangedRequestBody(RequestBody oldRequestBody, RequestBody newRequestBody) {
        this.oldRequestBody = oldRequestBody;
        this.newRequestBody = newRequestBody;
    }

    @Override
    public boolean isDiff() {
        return changeDescription || changeRequired || (changedContent != null && changedContent.isDiff());
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return !changeRequired && (changedContent == null || changedContent.isDiffBackwardCompatible(true));
    }
}
