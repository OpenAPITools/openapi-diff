package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedParameter;
import com.qdesrame.openapi.diff.model.ChangedSchema;
import com.qdesrame.openapi.diff.utils.RefPointer;
import io.swagger.oas.models.Components;
import io.swagger.oas.models.parameters.Parameter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

public class ParameterDiff implements Comparable<Parameter> {

    private Components leftComponents;
    private Components rightComponents;

    private ParameterDiff(Components left, Components right) {
        this.leftComponents = left;
        this.rightComponents = right;
    }

    public static ParameterDiff fromComponents(Components left, Components right) {
        return new ParameterDiff(left, right);
    }

    @Override
    public boolean compare(Parameter left, Parameter right) {
        return false;
    }

    public ChangedParameter diff(Parameter left, Parameter right) {
        ChangedParameter changedParameter = new ChangedParameter(right.getName(), right.getIn());
        RefPointer.Replace.parameter(this.leftComponents, left);
        RefPointer.Replace.parameter(this.rightComponents, right);
        boolean leftRequired = Optional.ofNullable(left.getRequired()).orElse(Boolean.FALSE);
        boolean rightRequired = Optional.ofNullable(right.getRequired()).orElse(Boolean.FALSE);
        changedParameter.setChangeRequired(leftRequired != rightRequired);
        String leftDescription = left.getDescription();
        String rightDescription = right.getDescription();
        if (StringUtils.isBlank(leftDescription)) {
            leftDescription = "";
        }
        if (StringUtils.isBlank(rightDescription)) {
            rightDescription = "";
        }
        changedParameter.setChangeDeprecated(!Boolean.TRUE.equals(left.getDeprecated()) && Boolean.TRUE.equals(left.getDeprecated()));
        changedParameter.setChangeDescription(Objects.equals(leftDescription, rightDescription));
        ChangedSchema changedSchema = SchemaDiff.fromComponents(leftComponents, rightComponents).diff(left.getSchema(), right.getSchema());
        if (changedSchema.isDiff()) {
            changedParameter.setChangedSchema(changedSchema);
        }
        return changedParameter;
    }
}
