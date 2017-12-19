package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.compare.schemaDiffResult.SchemaDiffResult;
import com.qdesrame.openapi.diff.model.Changed;
import io.swagger.oas.models.parameters.Parameter;

public class ParameterDiffResult implements Changed, Comparable<Parameter> {

    private Parameter left;
    private Parameter right;

    private String name;
    private String in;
    private boolean changeRequired;
    private boolean changeDescription;
    private boolean deprecated;
    private SchemaDiffResult schema;

    public ParameterDiffResult(String name, String in) {
        this.name = name;
        this.in = in;
    }

    @Override
    public boolean isDiff() {
        return false;
    }

    @Override
    public boolean compare(Parameter left, Parameter right) {
        return false;
    }

    public void setChangeRequired(boolean changeRequired) {
        this.changeRequired = changeRequired;
    }

    public boolean isChangeRequired() {
        return changeRequired;
    }

    public void setChangeDescription(boolean changeDescription) {
        this.changeDescription = changeDescription;
    }

    public boolean isChangeDescription() {
        return changeDescription;
    }

    public void setChangeDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public void setChangeSchema(SchemaDiffResult schema) {
        this.schema = schema;
    }

    public Parameter getLeftParameter() {
        return left;
    }

    public ParameterDiffResult setLeftParameter(Parameter left) {
        this.left = left;
        return this;
    }

    public Parameter getRightParameter() {
        return right;
    }

    public ParameterDiffResult setRightParameter(Parameter right) {
        this.right = right;
        return this;
    }
}
