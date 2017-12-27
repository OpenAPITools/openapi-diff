package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.parameters.RequestBody;

/**
 * Created by adarsh.sharma on 27/12/17.
 */
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

    public RequestBody getOldRequestBody() {
        return oldRequestBody;
    }

    public void setOldRequestBody(RequestBody oldRequestBody) {
        this.oldRequestBody = oldRequestBody;
    }

    public RequestBody getNewRequestBody() {
        return newRequestBody;
    }

    public void setNewRequestBody(RequestBody newRequestBody) {
        this.newRequestBody = newRequestBody;
    }

    public boolean isChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(boolean changeDescription) {
        this.changeDescription = changeDescription;
    }

    public boolean isChangeRequired() {
        return changeRequired;
    }

    public void setChangeRequired(boolean changeRequired) {
        this.changeRequired = changeRequired;
    }

    public ChangedContent getChangedContent() {
        return changedContent;
    }

    public void setChangedContent(ChangedContent changedContent) {
        this.changedContent = changedContent;
    }

    @Override
    public boolean isDiff() {
        return changeDescription || changeRequired || changedContent.isDiff();
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return !changeRequired && changedContent.isDiffBackwardCompatible(true);
    }
}
