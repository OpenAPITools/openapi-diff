package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.compare.ContentDiffResult;

public class ChangedResponse implements Changed {

    private String description;

    private ContentDiffResult changedContent;

    public String getDescription() {
        return description;
    }

    public ChangedResponse setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean isDiff() {
        return changedContent.isDiff();
    }

    public ContentDiffResult getChangedContent() {
        return changedContent;
    }

    public void setChangedContent(ContentDiffResult changedContent) {
        this.changedContent = changedContent;
    }
}
