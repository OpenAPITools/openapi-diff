package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.PathItem;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ChangedPaths implements ComposedChanged {

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
  public List<Changed> getChangedElements() {
    return new ArrayList<>(changed.values());
  }

  @Override
  public DiffResult isCoreChanged() {
    if (increased.isEmpty() && missing.isEmpty()) {
      return DiffResult.NO_CHANGES;
    }
    if (missing.isEmpty()) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
