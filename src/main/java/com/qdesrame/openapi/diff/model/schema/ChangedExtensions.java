package com.qdesrame.openapi.diff.model.schema;

import com.qdesrame.openapi.diff.model.Changed;
import com.qdesrame.openapi.diff.model.DiffContext;
import com.qdesrame.openapi.diff.model.DiffResult;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class ChangedExtensions implements Changed {
    private final Map<String, Object> oldExtensions;
    private final Map<String, Object> newExtensions;
    private final DiffContext context;

    private Map<String, Object> increased;
    private Map<String, Object> missing;
    private Map<String, Changed> changed;

    public ChangedExtensions(Map<String, Object> oldExtensions, Map<String, Object> newExtensions, DiffContext context) {
        this.oldExtensions = oldExtensions;
        this.newExtensions = newExtensions;
        this.context = context;
        this.increased = new LinkedHashMap<>();
        this.missing = new LinkedHashMap<>();
        this.changed = new LinkedHashMap<>();
    }

    @Override
    public DiffResult isChanged() {
        if (increased.isEmpty() && missing.isEmpty() && changed.isEmpty()) {
            return DiffResult.NO_CHANGES;
        }
        if (changed.values().stream().allMatch(Changed::isCompatible)) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }
}
