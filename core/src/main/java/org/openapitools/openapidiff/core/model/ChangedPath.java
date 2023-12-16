package org.openapitools.openapidiff.core.model;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.OPENAPI_ENDPOINTS_DECREASED;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChangedPath implements ComposedChanged {
  private final String pathUrl;
  private final PathItem oldPath;
  private final PathItem newPath;
  private final DiffContext context;
  Map<PathItem.HttpMethod, Operation> increased;
  Map<PathItem.HttpMethod, Operation> missing;
  List<ChangedOperation> changed;
  private ChangedExtensions extensions;

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
  public List<Changed> getChangedElements() {
    return Stream.concat(changed.stream(), Stream.of(extensions)).collect(Collectors.toList());
  }

  @Override
  public DiffResult isCoreChanged() {
    if (increased.isEmpty() && missing.isEmpty()) {
      return DiffResult.NO_CHANGES;
    }
    if (!missing.isEmpty()) {
      if (OPENAPI_ENDPOINTS_DECREASED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    return DiffResult.COMPATIBLE;
  }

  public String getPathUrl() {
    return this.pathUrl;
  }

  public PathItem getOldPath() {
    return this.oldPath;
  }

  public PathItem getNewPath() {
    return this.newPath;
  }

  public DiffContext getContext() {
    return this.context;
  }

  public Map<PathItem.HttpMethod, Operation> getIncreased() {
    return this.increased;
  }

  public Map<PathItem.HttpMethod, Operation> getMissing() {
    return this.missing;
  }

  public List<ChangedOperation> getChanged() {
    return this.changed;
  }

  public ChangedExtensions getExtensions() {
    return this.extensions;
  }

  public ChangedPath setIncreased(final Map<PathItem.HttpMethod, Operation> increased) {
    this.increased = increased;
    return this;
  }

  public ChangedPath setMissing(final Map<PathItem.HttpMethod, Operation> missing) {
    this.missing = missing;
    return this;
  }

  public ChangedPath setChanged(final List<ChangedOperation> changed) {
    this.changed = changed;
    return this;
  }

  public ChangedPath setExtensions(final ChangedExtensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedPath that = (ChangedPath) o;
    return Objects.equals(pathUrl, that.pathUrl)
        && Objects.equals(oldPath, that.oldPath)
        && Objects.equals(newPath, that.newPath)
        && Objects.equals(context, that.context)
        && Objects.equals(increased, that.increased)
        && Objects.equals(missing, that.missing)
        && Objects.equals(changed, that.changed)
        && Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        pathUrl, oldPath, newPath, context, increased, missing, changed, extensions);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedPath(pathUrl="
        + this.getPathUrl()
        + ", oldPath="
        + this.getOldPath()
        + ", newPath="
        + this.getNewPath()
        + ", context="
        + this.getContext()
        + ", increased="
        + this.getIncreased()
        + ", missing="
        + this.getMissing()
        + ", changed="
        + this.getChanged()
        + ", extensions="
        + this.getExtensions()
        + ")";
  }
}
