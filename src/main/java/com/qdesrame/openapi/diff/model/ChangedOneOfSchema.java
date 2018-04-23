package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by adarsh.sharma on 22/12/17.
 */
@Getter
@Setter
public class ChangedOneOfSchema implements Changed {
    private final Map<String, String> oldMapping;
    private final Map<String, String> newMapping;
    private final DiffContext context;

    private Map<String, Schema> increasedMapping;
    private Map<String, Schema> missingMapping;
    private Map<String, ChangedSchema> changedMapping;

    public ChangedOneOfSchema(Map<String, String> oldMapping, Map<String, String> newMapping, DiffContext context) {
        this.oldMapping = oldMapping;
        this.newMapping = newMapping;
        this.context = context;
    }

    @Override
    public DiffResult isChanged() {
        if (increasedMapping.size() == 0 && missingMapping.size() == 0 && changedMapping.size() == 0) {
            return DiffResult.NO_CHANGES;
        }
        if (((context.isRequest() && missingMapping.isEmpty()) || (context.isResponse() && increasedMapping.isEmpty()))
                && changedMapping.values().stream().allMatch(Changed::isCompatible)) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }
}
