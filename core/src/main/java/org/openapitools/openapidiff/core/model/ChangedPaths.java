package org.openapitools.openapidiff.core.model;

import io.swagger.v3.oas.models.PathItem;
import java.util.*;

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

  public Map<String, PathItem> getOldPathMap() {
    return this.oldPathMap;
  }

  public Map<String, PathItem> getNewPathMap() {
    return this.newPathMap;
  }

  public Map<String, PathItem> getIncreased() {
    return this.increased;
  }

  public Map<String, PathItem> getMissing() {
    return this.missing;
  }

  public Map<String, ChangedPath> getChanged() {
    return this.changed;
  }

  public void setIncreased(final Map<String, PathItem> increased) {
    this.increased = increased;
  }

  public void setMissing(final Map<String, PathItem> missing) {
    this.missing = missing;
  }

  public void setChanged(final Map<String, ChangedPath> changed) {
    this.changed = changed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedPaths that = (ChangedPaths) o;
    return Objects.equals(oldPathMap, that.oldPathMap)
        && Objects.equals(newPathMap, that.newPathMap)
        && Objects.equals(increased, that.increased)
        && Objects.equals(missing, that.missing)
        && Objects.equals(changed, that.changed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oldPathMap, newPathMap, increased, missing, changed);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedPaths(oldPathMap="
        + this.getOldPathMap()
        + ", newPathMap="
        + this.getNewPathMap()
        + ", increased="
        + this.getIncreased()
        + ", missing="
        + this.getMissing()
        + ", changed="
        + this.getChanged()
        + ")";
  }
}
