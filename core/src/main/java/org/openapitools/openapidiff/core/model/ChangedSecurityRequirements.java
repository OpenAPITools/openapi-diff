package org.openapitools.openapidiff.core.model;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SECURITY_REQUIREMENTS_DECREASED;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;

public class ChangedSecurityRequirements implements ComposedChanged {
  private List<SecurityRequirement> oldSecurityRequirements;
  private List<SecurityRequirement> newSecurityRequirements;
  private final DiffContext context;
  private List<SecurityRequirement> missing;
  private List<SecurityRequirement> increased;
  private List<ChangedSecurityRequirement> changed;

  public ChangedSecurityRequirements(
      List<SecurityRequirement> oldSecurityRequirements,
      List<SecurityRequirement> newSecurityRequirements,
      DiffContext context) {
    this.oldSecurityRequirements = oldSecurityRequirements;
    this.newSecurityRequirements = newSecurityRequirements;
    this.context = context;
    this.changed = new ArrayList<>();
  }

  @Override
  public List<Changed> getChangedElements() {
    return new ArrayList<>(changed);
  }

  @Override
  public DiffResult isCoreChanged() {
    if (CollectionUtils.isEmpty(missing) && CollectionUtils.isEmpty(increased)) {
      return DiffResult.NO_CHANGES;
    }
    if (CollectionUtils.isNotEmpty(missing)) {
      if (SECURITY_REQUIREMENTS_DECREASED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    return DiffResult.COMPATIBLE;
  }

  public void addMissing(SecurityRequirement securityRequirement) {
    if (this.missing == null) {
      this.missing = new ArrayList<>();
    }
    this.missing.add(securityRequirement);
  }

  public void addIncreased(SecurityRequirement securityRequirement) {
    if (this.increased == null) {
      this.increased = new ArrayList<>();
    }
    this.increased.add(securityRequirement);
  }

  public void addChanged(ChangedSecurityRequirement changedSecurityRequirement) {
    if (this.changed == null) {
      this.changed = new ArrayList<>();
    }
    this.changed.add(changedSecurityRequirement);
  }

  public List<SecurityRequirement> getOldSecurityRequirements() {
    return this.oldSecurityRequirements;
  }

  public List<SecurityRequirement> getNewSecurityRequirements() {
    return this.newSecurityRequirements;
  }

  public List<SecurityRequirement> getMissing() {
    return this.missing;
  }

  public List<SecurityRequirement> getIncreased() {
    return this.increased;
  }

  public List<ChangedSecurityRequirement> getChanged() {
    return this.changed;
  }

  public ChangedSecurityRequirements setOldSecurityRequirements(
      final List<SecurityRequirement> oldSecurityRequirements) {
    this.oldSecurityRequirements = oldSecurityRequirements;
    return this;
  }

  public ChangedSecurityRequirements setNewSecurityRequirements(
      final List<SecurityRequirement> newSecurityRequirements) {
    this.newSecurityRequirements = newSecurityRequirements;
    return this;
  }

  public ChangedSecurityRequirements setMissing(final List<SecurityRequirement> missing) {
    this.missing = missing;
    return this;
  }

  public ChangedSecurityRequirements setIncreased(final List<SecurityRequirement> increased) {
    this.increased = increased;
    return this;
  }

  public ChangedSecurityRequirements setChanged(final List<ChangedSecurityRequirement> changed) {
    this.changed = changed;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedSecurityRequirements that = (ChangedSecurityRequirements) o;
    return Objects.equals(oldSecurityRequirements, that.oldSecurityRequirements)
        && Objects.equals(newSecurityRequirements, that.newSecurityRequirements)
        && Objects.equals(missing, that.missing)
        && Objects.equals(increased, that.increased)
        && Objects.equals(changed, that.changed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        oldSecurityRequirements, newSecurityRequirements, missing, increased, changed);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedSecurityRequirements(oldSecurityRequirements="
        + this.getOldSecurityRequirements()
        + ", newSecurityRequirements="
        + this.getNewSecurityRequirements()
        + ", missing="
        + this.getMissing()
        + ", increased="
        + this.getIncreased()
        + ", changed="
        + this.getChanged()
        + ")";
  }
}
