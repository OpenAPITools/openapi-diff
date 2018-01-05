package com.qdesrame.openapi.diff.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by adarsh.sharma on 22/12/17.
 */
@Getter
@Setter
public class ChangedOneOfSchema implements RequestResponseChanged {
    private Map<String, String> oldMapping;
    private Map<String, String> newMapping;

    private Map<String, String> increasedMapping;
    private Map<String, String> missingMapping;
    private Map<String, ChangedSchema> changedMapping;

    public ChangedOneOfSchema(Map<String, String> oldMapping, Map<String, String> newMapping) {
        this.oldMapping = oldMapping;
        this.newMapping = newMapping;
    }

    @Override
    public boolean isDiff() {
        return increasedMapping.size() > 0 || missingMapping.size() > 0 || changedMapping.size() > 0;
    }

    @Override
    public boolean isDiffBackwardCompatible(boolean isRequest) {
        return ((isRequest && missingMapping.isEmpty()) || (!isRequest && increasedMapping.isEmpty()))
                && changedMapping.values().stream().allMatch(m -> m.isDiffBackwardCompatible(isRequest));
    }
}
