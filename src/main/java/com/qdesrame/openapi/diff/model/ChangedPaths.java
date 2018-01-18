package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.PathItem;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class ChangedPaths implements Changed {
    private Map<String, PathItem> oldPathMap;
    private Map<String, PathItem> newPathMap;

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
    public boolean isDiff() {
        return !increased.isEmpty() || !missing.isEmpty() || !changed.isEmpty();
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return missing.isEmpty() && changed.values().stream().allMatch(ChangedPath::isDiffBackwardCompatible);
    }
}
