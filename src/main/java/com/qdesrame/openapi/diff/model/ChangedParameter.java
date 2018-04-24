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
    private final DiffContext context;

    private boolean changeDescription;
    private boolean changeRequired;
    private boolean deprecated;
    private boolean changeStyle;
    private boolean changeExplode;
    private boolean changeAllowEmptyValue;
    private ChangedSchema changedSchema;
    private ChangedContent changedContent;

    public ChangedParameter(String name, String in, DiffContext context) {
        this.name = name;
        this.in = in;
        this.context = context;
    }

    @Override
    public DiffResult isChanged() {
        if (!changeDescription
                && !changeRequired
                && !deprecated
                && !changeAllowEmptyValue
                && !changeStyle
                && !changeExplode
                && (changedSchema == null || changedSchema.isUnchanged())
                && (changedContent == null || changedContent.isUnchanged())) {
            return DiffResult.NO_CHANGES;
        }
        if ((!changeRequired || Boolean.TRUE.equals(oldParameter.getRequired()))
                && (!changeAllowEmptyValue || Boolean.TRUE.equals(newParameter.getAllowEmptyValue()))
                && !changeStyle
                && !changeExplode
                && (changedSchema == null || changedSchema.isCompatible())
                && (changedContent == null || changedContent.isCompatible())) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }
}
