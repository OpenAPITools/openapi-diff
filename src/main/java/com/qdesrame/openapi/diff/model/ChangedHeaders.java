package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.headers.Header;

import java.util.Map;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
public class ChangedHeaders implements Changed {
    private Map<String, Header> oldHeaders;
    private Map<String, Header> newHeaders;

    private Map<String, Header> increased;
    private Map<String, Header> missing;
    private Map<String, ChangedHeader> changed;

    public ChangedHeaders(Map<String, Header> oldHeaders, Map<String, Header> newHeaders) {
        this.oldHeaders = oldHeaders;
        this.newHeaders = newHeaders;
    }

    public Map<String, Header> getOldHeaders() {
        return oldHeaders;
    }

    public void setOldHeaders(Map<String, Header> oldHeaders) {
        this.oldHeaders = oldHeaders;
    }

    public Map<String, Header> getNewHeaders() {
        return newHeaders;
    }

    public void setNewHeaders(Map<String, Header> newHeaders) {
        this.newHeaders = newHeaders;
    }

    public Map<String, Header> getIncreased() {
        return increased;
    }

    public void setIncreased(Map<String, Header> increased) {
        this.increased = increased;
    }

    public Map<String, Header> getMissing() {
        return missing;
    }

    public void setMissing(Map<String, Header> missing) {
        this.missing = missing;
    }

    public Map<String, ChangedHeader> getChanged() {
        return changed;
    }

    public void setChanged(Map<String, ChangedHeader> changed) {
        this.changed = changed;
    }

    @Override
    public boolean isDiff() {
        return !increased.isEmpty()
                || !missing.isEmpty()
                || (changed != null && !changed.isEmpty());
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return missing.isEmpty()
                && (changed == null || changed.values().stream().allMatch(c -> c.isDiffBackwardCompatible()));
    }
}
