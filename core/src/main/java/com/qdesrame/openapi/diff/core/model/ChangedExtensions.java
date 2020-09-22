package com.qdesrame.openapi.diff.core.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChangedExtensions implements ComposedChanged {
  private final Map<String, Object> oldExtensions;
  private final Map<String, Object> newExtensions;
  private final DiffContext context;
  private Map<String, Changed> increased;
  private Map<String, Changed> missing;
  private Map<String, Changed> changed;

  public ChangedExtensions(
      Map<String, Object> oldExtensions, Map<String, Object> newExtensions, DiffContext context) {
    this.oldExtensions = oldExtensions;
    this.newExtensions = newExtensions;
    this.context = context;
    this.increased = new LinkedHashMap<>();
    this.missing = new LinkedHashMap<>();
    this.changed = new LinkedHashMap<>();
  }

  @Override
  public List<Changed> getChangedElements() {
    return Stream.of(increased, missing, changed)
        .map(Map::values)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  @Override
  public DiffResult isCoreChanged() {
    return DiffResult.NO_CHANGES;
  }

  public Map<String, Object> getOldExtensions() {
    return this.oldExtensions;
  }

  public Map<String, Object> getNewExtensions() {
    return this.newExtensions;
  }

  public DiffContext getContext() {
    return this.context;
  }

  public Map<String, Changed> getIncreased() {
    return this.increased;
  }

  public Map<String, Changed> getMissing() {
    return this.missing;
  }

  public Map<String, Changed> getChanged() {
    return this.changed;
  }

  public void setIncreased(final Map<String, Changed> increased) {
    this.increased = increased;
  }

  public void setMissing(final Map<String, Changed> missing) {
    this.missing = missing;
  }

  public void setChanged(final Map<String, Changed> changed) {
    this.changed = changed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedExtensions that = (ChangedExtensions) o;
    return Objects.equals(oldExtensions, that.oldExtensions)
        && Objects.equals(newExtensions, that.newExtensions)
        && Objects.equals(context, that.context)
        && Objects.equals(increased, that.increased)
        && Objects.equals(missing, that.missing)
        && Objects.equals(changed, that.changed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oldExtensions, newExtensions, context, increased, missing, changed);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedExtensions(oldExtensions="
        + this.getOldExtensions()
        + ", newExtensions="
        + this.getNewExtensions()
        + ", context="
        + this.getContext()
        + ", increased="
        + this.getIncreased()
        + ", missing="
        + this.getMissing()
        + ", changed="
        + this.getChanged()
        + ")";
  }
}
