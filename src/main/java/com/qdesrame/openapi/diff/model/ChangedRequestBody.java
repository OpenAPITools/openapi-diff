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
    private final RequestBody oldRequestBody;
    private final RequestBody newRequestBody;
    private final DiffContext context;

    private boolean changeDescription;
    private boolean changeRequired;
    private ChangedContent changedContent;

    public ChangedRequestBody(RequestBody oldRequestBody, RequestBody newRequestBody, DiffContext context) {
        this.oldRequestBody = oldRequestBody;
        this.newRequestBody = newRequestBody;
        this.context = context;
    }

    @Override
    public DiffResult isChanged() {
        if (!changeDescription && !changeRequired && (changedContent == null || changedContent.isUnchanged())) {
            return DiffResult.NO_CHANGES;
        }
        if (!changeRequired && (changedContent == null || changedContent.isCompatible())) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }
}
