package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedParameter;
import com.qdesrame.openapi.diff.model.ChangedSchema;
import com.qdesrame.openapi.diff.utils.RefPointer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;

import java.util.Objects;
import java.util.Optional;

public class ParameterDiff implements Comparable<Parameter> {

    private Components leftComponents;
    private Components rightComponents;
    private OpenApiDiff openApiDiff;
    private ReferenceDiffCache<ChangedParameter> parameterReferenceDiffCache;

    public ParameterDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.leftComponents = openApiDiff.getOldSpecOpenApi() != null ? openApiDiff.getOldSpecOpenApi().getComponents() : null;
        this.rightComponents = openApiDiff.getNewSpecOpenApi() != null ? openApiDiff.getNewSpecOpenApi().getComponents() : null;
        this.parameterReferenceDiffCache = new ReferenceDiffCache<>();
    }

    @Override
    public boolean compare(Parameter left, Parameter right) {
        return false;
    }

    public Optional<ChangedParameter> diff(Parameter left, Parameter right) {
        String leftRef = left.get$ref();
        String rightRef = right.get$ref();
        boolean areBothRefParameters = leftRef != null && rightRef != null;
        if (areBothRefParameters) {
            Optional<ChangedParameter> changedParameterFromCache = parameterReferenceDiffCache.getFromCache(leftRef, rightRef);
            if (changedParameterFromCache.isPresent()) {
                return changedParameterFromCache;
            }
        }

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
        ChangedSchema changedSchema = openApiDiff.getSchemaDiff().diff(left.getSchema(), right.getSchema());
        if (changedSchema != null && changedSchema.isDiff()) {
            changedParameter.setChangedSchema(changedSchema);
        }
        openApiDiff.getContentDiff().diff(left.getContent(), right.getContent())
                .ifPresent(changedParameter::setChangedContent);

        if (areBothRefParameters) {
            parameterReferenceDiffCache.addToCache(leftRef, rightRef, changedParameter);
        }

        return changedParameter.isDiff() ? Optional.of(changedParameter) : Optional.empty();
    }

    private boolean getBooleanDiff(Boolean left, Boolean right) {
        boolean leftRequired = Optional.ofNullable(left).orElse(Boolean.FALSE);
        boolean rightRequired = Optional.ofNullable(right).orElse(Boolean.FALSE);
        return leftRequired != rightRequired;
    }
}
