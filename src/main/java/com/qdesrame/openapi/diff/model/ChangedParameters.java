package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChangedParameters implements Changed {
    private final List<Parameter> oldParameterList;
    private final List<Parameter> newParameterList;
    private final DiffContext context;

    private List<Parameter> increased;
    private List<Parameter> missing;
    private List<ChangedParameter> changed;

    public ChangedParameters(List<Parameter> oldParameterList, List<Parameter> newParameterList, DiffContext context) {
        this.oldParameterList = oldParameterList;
        this.newParameterList = newParameterList;
        this.context = context;
        this.increased = new ArrayList<>();
        this.missing = new ArrayList<>();
        this.changed = new ArrayList<>();
    }

    @Override
    public DiffResult isChanged() {
        if (increased.isEmpty() && missing.isEmpty() && changed.isEmpty()) {
            return DiffResult.NO_CHANGES;
        }
        if (increased.stream().noneMatch(Parameter::getRequired) && missing.isEmpty()
                && changed.stream().allMatch(Changed::isCompatible)) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }
}
