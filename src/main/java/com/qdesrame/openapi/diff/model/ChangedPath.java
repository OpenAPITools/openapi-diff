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
    Map<PathItem.HttpMethod, Operation> increased;
    Map<PathItem.HttpMethod, Operation> missing;
    List<ChangedOperation> changed;
    private String pathUrl;
    private PathItem oldPath;
    private PathItem newPath;

    public ChangedPath(String pathUrl, PathItem oldPath, PathItem newPath) {
        this.pathUrl = pathUrl;
        this.oldPath = oldPath;
        this.newPath = newPath;
        this.increased = new LinkedHashMap<>();
        this.missing = new LinkedHashMap<>();
        this.changed = new ArrayList<>();
    }

    @Override
    public boolean isDiff() {
        return !increased.isEmpty() || !missing.isEmpty() || !changed.isEmpty();
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return missing.isEmpty() && changed.stream().allMatch(ChangedOperation::isDiffBackwardCompatible);
    }
}
