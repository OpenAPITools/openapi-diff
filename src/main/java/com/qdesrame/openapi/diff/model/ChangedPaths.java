package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.PathItem;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class ChangedPaths implements Changed {
    private final Map<String, PathItem> oldPathMap;
    private final Map<String, PathItem> newPathMap;

    private Map<String, PathItem> increased;
    private Map<String, PathItem> missing;
    private Map<String, ChangedPath> changed;

    public ChangedPaths(Map<String, PathItem> oldPathMap, Map<String, PathItem> newPathMap) {
        this.oldPathMap = oldPathMap;
        this.newPathMap = newPathMap;
        this.increased = new LinkedHashMap<>();
        this.missing = new LinkedHashMap<>();
        this.changed = new LinkedHashMap<>();
    }

    @Override
    public DiffResult isChanged() {
        if (increased.isEmpty() && missing.isEmpty() && changed.isEmpty()) {
            return DiffResult.NO_CHANGES;
        }
        if (missing.isEmpty() && changed.values().stream().allMatch(Changed::isCompatible)) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }
}
