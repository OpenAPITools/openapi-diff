package com.qdesrame.openapi.diff.core.model;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChangedSecurityRequirement implements ComposedChanged {
  private SecurityRequirement oldSecurityRequirement;
  private SecurityRequirement newSecurityRequirement;
  private SecurityRequirement missing;
  private SecurityRequirement increased;
  private List<ChangedSecurityScheme> changed;

  public ChangedSecurityRequirement(
      SecurityRequirement oldSecurityRequirement, SecurityRequirement newSecurityRequirement) {
    this.oldSecurityRequirement = oldSecurityRequirement;
    this.newSecurityRequirement = newSecurityRequirement;
    this.changed = new ArrayList<>();
  }

  @Override
  public List<Changed> getChangedElements() {
    return new ArrayList<>(changed);
  }

  @Override
  public DiffResult isCoreChanged() {
    if (increased == null && missing == null) {
      return DiffResult.NO_CHANGES;
    }
    if (increased == null) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }

  public void addMissing(String key, List<String> scopes) {
    if (missing == null) {
      missing = new SecurityRequirement();
    }
    missing.put(key, scopes);
  }

  public void addIncreased(String key, List<String> scopes) {
    if (increased == null) {
      increased = new SecurityRequirement();
    }
    increased.put(key, scopes);
  }

  public void addChanged(ChangedSecurityScheme changedSecurityScheme) {
    changed.add(changedSecurityScheme);
  }

  public SecurityRequirement getOldSecurityRequirement() {
    return this.oldSecurityRequirement;
  }

  public SecurityRequirement getNewSecurityRequirement() {
    return this.newSecurityRequirement;
  }

  public SecurityRequirement getMissing() {
    return this.missing;
  }

  public SecurityRequirement getIncreased() {
    return this.increased;
  }

  public List<ChangedSecurityScheme> getChanged() {
    return this.changed;
  }

  public ChangedSecurityRequirement setOldSecurityRequirement(
      final SecurityRequirement oldSecurityRequirement) {
    this.oldSecurityRequirement = oldSecurityRequirement;
    return this;
  }

  public ChangedSecurityRequirement setNewSecurityRequirement(
      final SecurityRequirement newSecurityRequirement) {
    this.newSecurityRequirement = newSecurityRequirement;
    return this;
  }

  public ChangedSecurityRequirement setMissing(final SecurityRequirement missing) {
    this.missing = missing;
    return this;
  }

  public ChangedSecurityRequirement setIncreased(final SecurityRequirement increased) {
    this.increased = increased;
    return this;
  }

  public ChangedSecurityRequirement setChanged(final List<ChangedSecurityScheme> changed) {
    this.changed = changed;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedSecurityRequirement that = (ChangedSecurityRequirement) o;
    return Objects.equals(oldSecurityRequirement, that.oldSecurityRequirement)
        && Objects.equals(newSecurityRequirement, that.newSecurityRequirement)
        && Objects.equals(missing, that.missing)
        && Objects.equals(increased, that.increased)
        && Objects.equals(changed, that.changed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        oldSecurityRequirement, newSecurityRequirement, missing, increased, changed);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedSecurityRequirement(oldSecurityRequirement="
        + this.getOldSecurityRequirement()
        + ", newSecurityRequirement="
        + this.getNewSecurityRequirement()
        + ", missing="
        + this.getMissing()
        + ", increased="
        + this.getIncreased()
        + ", changed="
        + this.getChanged()
        + ")";
  }
}
