package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.parameters.Parameter;


public class ChangedParameter implements Changed {
    private Parameter oldParameter;
    private Parameter newParameter;

    private String name;
    private String in;

    private boolean changeDescription;
    private boolean changeRequired;
    private boolean deprecated;
    private boolean changeStyle;
    private boolean changeExplode;
    private boolean changeAllowEmptyValue;
    private ChangedSchema changedSchema;
    private ChangedContent changedContent;

    public ChangedParameter(String name, String in) {
        this.name = name;
        this.in = in;
    }

    @Override
    public boolean isDiff() {
        return changeDescription
                || changeRequired
                || deprecated
                || changeAllowEmptyValue
                || changeStyle
                || changeExplode
                || (changedSchema != null && changedSchema.isDiff())
                || (changedContent != null && changedContent.isDiff());
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return (!changeRequired || Boolean.TRUE.equals(oldParameter.getRequired()))
                && (!changeAllowEmptyValue || Boolean.TRUE.equals(newParameter.getAllowEmptyValue()))
                && !changeStyle
                && !changeExplode
                && (changedSchema == null || changedSchema.isDiffBackwardCompatible(true))
                && (changedContent == null || changedContent.isDiffBackwardCompatible(true));
    }

    public Parameter getOldParameter() {
        return oldParameter;
    }

    public void setOldParameter(Parameter oldParameter) {
        this.oldParameter = oldParameter;
    }

    public Parameter getNewParameter() {
        return newParameter;
    }

    public void setNewParameter(Parameter newParameter) {
        this.newParameter = newParameter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public boolean isChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(boolean changeDescription) {
        this.changeDescription = changeDescription;
    }

    public boolean isChangeRequired() {
        return changeRequired;
    }

    public void setChangeRequired(boolean changeRequired) {
        this.changeRequired = changeRequired;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public boolean isChangeStyle() {
        return changeStyle;
    }

    public void setChangeStyle(boolean changeStyle) {
        this.changeStyle = changeStyle;
    }

    public boolean isChangeExplode() {
        return changeExplode;
    }

    public void setChangeExplode(boolean changeExplode) {
        this.changeExplode = changeExplode;
    }

    public boolean isChangeAllowEmptyValue() {
        return changeAllowEmptyValue;
    }

    public void setChangeAllowEmptyValue(boolean changeAllowEmptyValue) {
        this.changeAllowEmptyValue = changeAllowEmptyValue;
    }

    public ChangedSchema getChangedSchema() {
        return changedSchema;
    }

    public void setChangedSchema(ChangedSchema changedSchema) {
        this.changedSchema = changedSchema;
    }

    public ChangedContent getChangedContent() {
        return changedContent;
    }

    public void setChangedContent(ChangedContent changedContent) {
        this.changedContent = changedContent;
    }
}
