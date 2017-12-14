package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.Changed;
import io.swagger.oas.models.parameters.Parameter;

import java.util.ArrayList;
import java.util.List;

public class ParametersDiffResult implements Changed {

    private List<Parameter> increased;
    private List<Parameter> missing;
    private List<ParameterDiffResult> changed;

    public ParametersDiffResult() {
        increased = new ArrayList<>();
        missing = new ArrayList<>();
        changed = new ArrayList<>();
    }

    public List<Parameter> getIncreased() {
        return increased;
    }

    public List<Parameter> getMissing() {
        return missing;
    }

    public List<ParameterDiffResult> getChanged() {
        return changed;
    }

    @Override
    public boolean isDiff() {
        return !increased.isEmpty() || !missing.isEmpty() || !changed.isEmpty();
    }
}
