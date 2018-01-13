package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedParameter;
import com.qdesrame.openapi.diff.model.ChangedSchema;
import com.qdesrame.openapi.diff.utils.RefPointer;
import com.qdesrame.openapi.diff.utils.RefType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;

import java.util.Objects;
import java.util.Optional;

public class ParameterDiff extends ReferenceDiffCache<Parameter, ChangedParameter> {

    private Components leftComponents;
    private Components rightComponents;
    private OpenApiDiff openApiDiff;
    private static RefPointer<Parameter> refPointer = new RefPointer<>(RefType.PARAMETERS);

    public ParameterDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.leftComponents = openApiDiff.getOldSpecOpenApi() != null ? openApiDiff.getOldSpecOpenApi().getComponents() : null;
        this.rightComponents = openApiDiff.getNewSpecOpenApi() != null ? openApiDiff.getNewSpecOpenApi().getComponents() : null;
    }

    public Optional<ChangedParameter> diff(Parameter left, Parameter right) {
        return cachedDiff(left, right, left.get$ref(), right.get$ref());
    }

    @Override
    protected Optional<ChangedParameter> computeDiff(Parameter left, Parameter right) {
        ChangedParameter changedParameter = new ChangedParameter(right.getName(), right.getIn());
        left = refPointer.resolveRef(this.leftComponents, left, left.get$ref());
        right = refPointer.resolveRef(this.rightComponents, right, right.get$ref());

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
