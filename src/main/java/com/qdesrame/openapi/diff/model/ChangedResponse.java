package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.media.Content;

public class ChangedResponse implements Changed {
    private String description;
    private Content oldContent;
    private Content newContent;
    private ChangedContent changedContent;

    public ChangedResponse(String description, Content oldContent, Content newContent) {
        this.description = description;
        this.oldContent = oldContent;
        this.newContent = newContent;
    }

    public String getDescription() {
        return description;
    }

    public ChangedResponse setDescription(String description) {
        this.description = description;
        return this;
    }

    public Content getOldContent() {
        return oldContent;
    }

    public Content getNewContent() {
        return newContent;
    }

    @Override
    public boolean isDiff() {
        return changedContent.isDiff();
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return changedContent.isDiffBackwardCompatible(false);
    }

    public ChangedContent getChangedContent() {
        return changedContent;
    }

    public void setChangedContent(ChangedContent changedContent) {
        this.changedContent = changedContent;
    }
}
