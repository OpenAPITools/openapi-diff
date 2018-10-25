package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.model.schema.ChangedExtensions;
import com.qdesrame.openapi.diff.utils.ChangedUtils;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

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
  private ChangedExtensions changedExtensions;

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
    if (increased.isEmpty()
        && missing.isEmpty()
        && changed.isEmpty()
        && ChangedUtils.isUnchanged(changedExtensions)) {
      return DiffResult.NO_CHANGES;
    }
    if (missing.isEmpty()
        && changed.stream().allMatch(Changed::isCompatible)
        && ChangedUtils.isCompatible(changedExtensions)) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
