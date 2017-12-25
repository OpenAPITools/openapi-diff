package com.qdesrame.openapi.diff.model;

import io.swagger.oas.models.media.Content;
import io.swagger.oas.models.media.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adarsh.sharma on 22/12/17.
 */
public class ChangedContent implements RequestResponseChanged {
    private Content oldContent;
    private Content newContent;

    private Map<String, MediaType> increased;
    private Map<String, MediaType> missing;
    private Map<String, ChangedMediaType> changed;

    public ChangedContent(Content oldContent, Content newContent) {
        this.oldContent = oldContent;
        this.newContent = newContent;
        this.increased = new HashMap<>();
        this.missing = new HashMap<>();
        this.changed = new HashMap<>();
    }

    public Content getOldContent() {
        return oldContent;
    }

    public Content getNewContent() {
        return newContent;
    }

    public Map<String, MediaType> getIncreased() {
        return increased;
    }

    public void setIncreased(Map<String, MediaType> increased) {
        this.increased = increased;
    }

    public Map<String, MediaType> getMissing() {
        return missing;
    }

    public void setMissing(Map<String, MediaType> missing) {
        this.missing = missing;
    }

    public Map<String, ChangedMediaType> getChanged() {
        return changed;
    }

    public void setChanged(Map<String, ChangedMediaType> changed) {
        this.changed = changed;
    }

    @Override
    public boolean isDiff() {
        return !increased.isEmpty() || !missing.isEmpty() || !changed.isEmpty();
    }

    @Override
    public boolean isDiffBackwardCompatible(boolean isRequest) {
        return ((isRequest && missing.isEmpty()) || (!isRequest && increased.isEmpty()))
                && changed.values().stream().allMatch(c -> c.isDiffBackwardCompatible(isRequest));
    }

}
