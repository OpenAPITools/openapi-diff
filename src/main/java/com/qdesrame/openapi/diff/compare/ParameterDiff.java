package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedParameter;
import com.qdesrame.openapi.diff.model.ChangedSchema;
import com.qdesrame.openapi.diff.utils.RefPointer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;

import java.util.Objects;
import java.util.Optional;

public class ParameterDiff extends ReferenceDiffCache<Parameter, ChangedParameter> implements Comparable<Parameter> {

    private Components leftComponents;
    private Components rightComponents;
    private OpenApiDiff openApiDiff;

    public ParameterDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.leftComponents = openApiDiff.getOldSpecOpenApi() != null ? openApiDiff.getOldSpecOpenApi().getComponents() : null;
        this.rightComponents = openApiDiff.getNewSpecOpenApi() != null ? openApiDiff.getNewSpecOpenApi().getComponents() : null;
    }

    @Override
    public boolean compare(Parameter left, Parameter right) {
        return false;
    }

    public Optional<ChangedParameter> diff(Parameter left, Parameter right) {
        return super.cachedDiff(left, right, left.get$ref(), right.get$ref());
    }

    @Override
    protected Optional<ChangedParameter> computeDiff(Parameter left, Parameter right) {
        ChangedParameter changedParameter = new ChangedParameter(right.getName(), right.getIn());
        RefPointer.Replace.parameter(this.leftComponents, left);
        RefPointer.Replace.parameter(this.rightComponents, right);

        changedParameter.setOldParameter(left);
        changedParameter.setNewParameter(right);

        changedParameter.setChangeDescription(!Objects.equals(left.getDescription(), right.getDescription()));
        changedParameter.setChangeRequired(getBooleanDiff(left.getRequired(), right.getRequired()));
        changedParameter.setDeprecated(!Boolean.TRUE.equals(left.getDeprecated()) && Boolean.TRUE.equals(right.getDeprecated()));
        changedParameter.setChangeAllowEmptyValue(getBooleanDiff(left.getAllowEmptyValue(), right.getAllowEmptyValue()));
        changedParameter.setChangeStyle(!Objects.equals(left.getStyle(), right.getStyle()));
        changedParameter.setChangeExplode(getBooleanDiff(left.getExplode(), right.getExplode()));
        Optional<ChangedSchema> changedSchema = openApiDiff.getSchemaDiff().diff(left.getSchema(), right.getSchema());
        if (changedSchema.isPresent()) {
            changedParameter.setChangedSchema(changedSchema.get());
        }
        openApiDiff.getContentDiff().diff(left.getContent(), right.getContent())
                .ifPresent(changedParameter::setChangedContent);

        return changedParameter.isDiff() ? Optional.of(changedParameter) : Optional.empty();
    }

    private boolean getBooleanDiff(Boolean left, Boolean right) {
        boolean leftRequired = Optional.ofNullable(left).orElse(Boolean.FALSE);
        boolean rightRequired = Optional.ofNullable(right).orElse(Boolean.FALSE);
        return leftRequired != rightRequired;
    }
}
