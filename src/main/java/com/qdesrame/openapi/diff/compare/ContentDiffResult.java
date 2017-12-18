package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.Changed;
import com.qdesrame.openapi.diff.model.ChangedMediaType;
import io.swagger.oas.models.media.MediaType;

import java.util.HashMap;
import java.util.Map;

public class ContentDiffResult implements Changed {

    private Map<String, MediaType> increased;
    private Map<String, MediaType> missing;
    private Map<String, ChangedMediaType> changed;

    public ContentDiffResult() {
        increased = new HashMap<>();
        missing = new HashMap<>();
        changed = new HashMap<>();
    }

    public Map<String, MediaType> getIncreased() {
        return increased;
    }

    public ContentDiffResult setIncreased(Map<String, MediaType> increased) {
        this.increased = increased;
        return this;
    }

    public Map<String, MediaType> getMissing() {
        return missing;
    }

    public ContentDiffResult setMissing(Map<String, MediaType> missing) {
        this.missing = missing;
        return this;
    }

    public Map<String, ChangedMediaType> getChanged() {
        return changed;
    }

    public ContentDiffResult setChanged(Map<String, ChangedMediaType> changed) {
        this.changed = changed;
        return this;
    }

    @Override
    public boolean isDiff() {
        return !increased.isEmpty() || !missing.isEmpty() || !changed.isEmpty();
    }

}
