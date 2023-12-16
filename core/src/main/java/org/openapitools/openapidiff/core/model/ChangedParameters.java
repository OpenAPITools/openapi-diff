package org.openapitools.openapidiff.core.model;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_PARAMS_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_PARAMS_REQUIRED_INCREASED;

import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChangedParameters implements ComposedChanged {
  private final List<Parameter> oldParameterList;
  private final List<Parameter> newParameterList;
  private final DiffContext context;
  private List<Parameter> increased;
  private List<Parameter> missing;
  private List<ChangedParameter> changed;

  public ChangedParameters(
      List<Parameter> oldParameterList, List<Parameter> newParameterList, DiffContext context) {
    this.oldParameterList = oldParameterList;
    this.newParameterList = newParameterList;
    this.context = context;
    this.increased = new ArrayList<>();
    this.missing = new ArrayList<>();
    this.changed = new ArrayList<>();
  }

  @Override
  public List<Changed> getChangedElements() {
    return new ArrayList<>(changed);
  }

  @Override
  public DiffResult isCoreChanged() {
    if (increased.isEmpty() && missing.isEmpty()) {
      return DiffResult.NO_CHANGES;
    }
    if (!missing.isEmpty()) {
      if (REQUEST_PARAMS_DECREASED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    if (increased.stream().anyMatch(p -> p.getRequired() != null && p.getRequired())) {
      if (REQUEST_PARAMS_REQUIRED_INCREASED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    return DiffResult.COMPATIBLE;
  }

  public List<Parameter> getOldParameterList() {
    return this.oldParameterList;
  }

  public List<Parameter> getNewParameterList() {
    return this.newParameterList;
  }

  public DiffContext getContext() {
    return this.context;
  }

  public List<Parameter> getIncreased() {
    return this.increased;
  }

  public List<Parameter> getMissing() {
    return this.missing;
  }

  public List<ChangedParameter> getChanged() {
    return this.changed;
  }

  public ChangedParameters setIncreased(final List<Parameter> increased) {
    this.increased = increased;
    return this;
  }

  public ChangedParameters setMissing(final List<Parameter> missing) {
    this.missing = missing;
    return this;
  }

  public ChangedParameters setChanged(final List<ChangedParameter> changed) {
    this.changed = changed;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedParameters that = (ChangedParameters) o;
    return Objects.equals(oldParameterList, that.oldParameterList)
        && Objects.equals(newParameterList, that.newParameterList)
        && Objects.equals(context, that.context)
        && Objects.equals(increased, that.increased)
        && Objects.equals(missing, that.missing)
        && Objects.equals(changed, that.changed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oldParameterList, newParameterList, context, increased, missing, changed);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedParameters(oldParameterList="
        + this.getOldParameterList()
        + ", newParameterList="
        + this.getNewParameterList()
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
