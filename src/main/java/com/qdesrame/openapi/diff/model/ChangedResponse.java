package com.qdesrame.openapi.diff.model;

import java.util.Map;

public class ChangedResponse implements Changed{

    private String description;
    private Map<String, ChangedMediaType> changedMediaTypes;

    public String getDescription() {
        return description;
    }

    public ChangedResponse setDescription(String description) {
        this.description = description;
        return this;
    }

    public Map<String, ChangedMediaType> getChangedMediaTypes() {
        return changedMediaTypes;
    }

    public void setChangedMediaTypes(Map<String,ChangedMediaType> changedMediaTypes) {
        this.changedMediaTypes = changedMediaTypes;
    }

    @Override
    public boolean isDiff() {
        return !changedMediaTypes.isEmpty();
    }

}
