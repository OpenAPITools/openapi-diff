package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
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
    if (increased
            .stream()
            .noneMatch(
                p -> {
                  return p.getRequired() != null && p.getRequired();
                })
        && missing.isEmpty()) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
