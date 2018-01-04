package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
