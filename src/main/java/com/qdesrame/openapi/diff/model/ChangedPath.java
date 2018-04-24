package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ChangedPath implements Changed {
    private final String pathUrl;
    private final PathItem oldPath;
    private final PathItem newPath;
    private final DiffContext context;

    Map<PathItem.HttpMethod, Operation> increased;
    Map<PathItem.HttpMethod, Operation> missing;
    List<ChangedOperation> changed;

    public ChangedPath(String pathUrl, PathItem oldPath, PathItem newPath, DiffContext context) {
        this.pathUrl = pathUrl;
        this.oldPath = oldPath;
        this.newPath = newPath;
        this.context = context;
        this.increased = new LinkedHashMap<>();
        this.missing = new LinkedHashMap<>();
        this.changed = new ArrayList<>();
    }

    @Override
    public DiffResult isChanged() {
        if (increased.isEmpty() && missing.isEmpty() && changed.isEmpty()) {
            return DiffResult.NO_CHANGES;
        }
        if (missing.isEmpty() && changed.stream().allMatch(Changed::isCompatible)) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }
}
