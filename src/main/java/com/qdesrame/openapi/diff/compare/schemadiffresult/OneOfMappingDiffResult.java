package com.qdesrame.openapi.diff.compare.schemadiffresult;

import com.qdesrame.openapi.diff.model.Changed;

import java.util.Map;

/**
 * Created by adarsh.sharma on 21/12/17.
 */
public class OneOfMappingDiffResult implements Changed {
    private Map<String, String> increasedMapping;
    private Map<String, String> missingMapping;
    private Map<String, SchemaDiffResult> changedMapping;

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

    public Map<String, SchemaDiffResult> getChangedMapping() {
        return changedMapping;
    }

    public void setChangedMapping(Map<String, SchemaDiffResult> changedMapping) {
        this.changedMapping = changedMapping;
    }

    @Override
    public boolean isDiff() {
        return increasedMapping.size() > 0 || missingMapping.size() > 0 || changedMapping.size() > 0;
    }
}
