package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedParameter;
import com.qdesrame.openapi.diff.utils.RefPointer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

public class ParameterDiff implements Comparable<Parameter> {

    private Components leftComponents;
    private Components rightComponents;
    private OpenApiDiff openApiDiff;

    public ParameterDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.leftComponents = openApiDiff.getOldSpecOpenApi() != null? openApiDiff.getOldSpecOpenApi().getComponents(): null;
        this.rightComponents = openApiDiff.getNewSpecOpenApi() != null? openApiDiff.getNewSpecOpenApi().getComponents(): null;
    }

    @Override
    public boolean compare(Parameter left, Parameter right) {
        return false;
    }

    public ChangedParameter diff(Parameter left, Parameter right) {
        ChangedParameter changedParameter = new ChangedParameter(right.getName(), right.getIn());
        RefPointer.Replace.parameter(this.leftComponents, left);
        RefPointer.Replace.parameter(this.rightComponents, right);
        changedParameter.setLeftParameter(left);
        changedParameter.setRightParameter(right);
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
        changedParameter.setChangeDeprecated(!Boolean.TRUE.equals(left.getDeprecated()) && Boolean.TRUE.equals(right.getDeprecated()));
        changedParameter.setChangeDescription(!Objects.equals(leftDescription, rightDescription));
        changedParameter.setChangedSchema(openApiDiff.getSchemaDiff().diff(left.getSchema(), right.getSchema()));
        return changedParameter;
    }
}
