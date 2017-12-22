package com.qdesrame.openapi.diff.model;

import java.util.Map;

/**
 * Created by adarsh.sharma on 22/12/17.
 */
public class ChangedOneOfSchema implements Changed {
    private Map<String, String> oldMapping;
    private Map<String, String> newMapping;

    private Map<String, String> increasedMapping;
    private Map<String, String> missingMapping;
    private Map<String, ChangedSchema> changedMapping;

    public ChangedOneOfSchema(Map<String, String> oldMapping, Map<String, String> newMapping) {
        this.oldMapping = oldMapping;
        this.newMapping = newMapping;
    }

    public Map<String, String> getOldMapping() {
        return oldMapping;
    }

    public Map<String, String> getNewMapping() {
        return newMapping;
    }

    public Map<String, String> getIncreasedMapping() {
        return increasedMapping;
    }

    public void setIncreasedMapping(Map<String, String> increasedMapping) {
        this.increasedMapping = increasedMapping;
    }

    public Map<String, String> getMissingMapping() {
        return missingMapping;
    }

    public void setMissingMapping(Map<String, String> missingMapping) {
        this.missingMapping = missingMapping;
    }

    public Map<String, ChangedSchema> getChangedMapping() {
        return changedMapping;
    }

    public void setChangedMapping(Map<String, ChangedSchema> changedMapping) {
        this.changedMapping = changedMapping;
    }

    @Override
    public boolean isDiff() {
        return increasedMapping.size() > 0 || missingMapping.size() > 0 || changedMapping.size() > 0;
    }
}
