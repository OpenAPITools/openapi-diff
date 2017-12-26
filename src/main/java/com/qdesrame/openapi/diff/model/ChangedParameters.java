package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.parameters.Parameter;

import java.util.ArrayList;
import java.util.List;

public class ChangedParameters implements Changed {
    private List<Parameter> oldParameterList;
    private List<Parameter> newParameterList;

    private List<Parameter> increased;
    private List<Parameter> missing;
    private List<ChangedParameter> changed;

    public ChangedParameters(List<Parameter> oldParameterList, List<Parameter> newParameterList) {
        this.oldParameterList = oldParameterList;
        this.newParameterList = newParameterList;
        this.increased = new ArrayList<>();
        this.missing = new ArrayList<>();
        this.changed = new ArrayList<>();
    }

    public List<Parameter> getOldParameterList() {
        return oldParameterList;
    }

    public List<Parameter> getNewParameterList() {
        return newParameterList;
    }

    public List<Parameter> getIncreased() {
        return increased;
    }

    public List<Parameter> getMissing() {
        return missing;
    }

    public List<ChangedParameter> getChanged() {
        return changed;
    }

    @Override
    public boolean isDiff() {
        return !increased.isEmpty() || !missing.isEmpty() || !changed.isEmpty();
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return increased.stream().noneMatch(p -> p.getRequired())
                && missing.isEmpty()
                && changed.stream().allMatch(p -> p.isDiffBackwardCompatible());
    }
}
