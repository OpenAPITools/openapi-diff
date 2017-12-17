package com.qdesrame.openapi.diff.model;

import io.swagger.oas.models.media.MediaType;

import java.util.Map;

public class ChangedResponse implements Changed{

    private String description;
    private Map<String, ChangedMediaType> changedMediaTypes;
    private Map<String, MediaType> missingMediaTypes;
    private Map<String, MediaType> addMediaTypes;

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

    public void setMissingMediaTypes(Map<String, MediaType> missingMediaTypes) {
        this.missingMediaTypes = missingMediaTypes;
    }

    public void setAddMediaTypes(Map<String, MediaType> addMediaTypes) {
        this.addMediaTypes = addMediaTypes;
    }
}
