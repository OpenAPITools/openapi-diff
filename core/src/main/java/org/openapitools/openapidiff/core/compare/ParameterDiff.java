package org.openapitools.openapidiff.core.compare;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.HashSet;
import java.util.Objects;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedExample;
import org.openapitools.openapidiff.core.model.ChangedExamples;
import org.openapitools.openapidiff.core.model.ChangedParameter;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredBuilder;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;
import org.openapitools.openapidiff.core.utils.RefPointer;
import org.openapitools.openapidiff.core.utils.RefType;

public class ParameterDiff extends ReferenceDiffCache<Parameter, ChangedParameter> {

  private static final RefPointer<Parameter> refPointer = new RefPointer<>(RefType.PARAMETERS);
  private final Components leftComponents;
  private final Components rightComponents;
  private final OpenApiDiff openApiDiff;

  public ParameterDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
    this.leftComponents =
        openApiDiff.getOldSpecOpenApi() != null
            ? openApiDiff.getOldSpecOpenApi().getComponents()
            : null;
    this.rightComponents =
        openApiDiff.getNewSpecOpenApi() != null
            ? openApiDiff.getNewSpecOpenApi().getComponents()
            : null;
  }

  public DeferredChanged<ChangedParameter> diff(
      Parameter left, Parameter right, DiffContext context) {
    return cachedDiff(new HashSet<>(), left, right, left.get$ref(), right.get$ref(), context);
  }

  @Override
  protected DeferredChanged<ChangedParameter> computeDiff(
      HashSet<String> refSet, Parameter left, Parameter right, DiffContext context) {
    left = refPointer.resolveRef(this.leftComponents, left, left.get$ref());
    right = refPointer.resolveRef(this.rightComponents, right, right.get$ref());

    DeferredBuilder<Changed> builder = new DeferredBuilder<>();

    ChangedParameter changedParameter =
        new ChangedParameter(right.getName(), right.getIn(), context)
            .setOldParameter(left)
            .setNewParameter(right)
            .setChangeRequired(!Objects.equals(left.getRequired(), right.getRequired()))
            .setDeprecated(!Objects.equals(left.getDeprecated(), right.getDeprecated()))
            .setChangeAllowEmptyValue(
                !Objects.equals(left.getAllowEmptyValue(), right.getAllowEmptyValue()))
            .setChangeStyle(!Objects.equals(left.getStyle(), right.getStyle()))
            .setChangeExplode(!Objects.equals(left.getExplode(), right.getExplode()))
            .setExamples(new ChangedExamples(left.getExamples(), right.getExamples()))
            .setExample(new ChangedExample(left.getExample(), right.getExample()));
    builder
        .with(
            openApiDiff
                .getSchemaDiff()
                .diff(left.getSchema(), right.getSchema(), context.copyWithRequired(true)))
        .ifPresent(changedParameter::setSchema);
    builder
        .with(
            openApiDiff
                .getMetadataDiff()
                .diff(left.getDescription(), right.getDescription(), context))
        .ifPresent(changedParameter::setDescription);
    builder
        .with(openApiDiff.getContentDiff().diff(left.getContent(), right.getContent(), context))
        .ifPresent(changedParameter::setContent);
    builder
        .with(
            openApiDiff
                .getExtensionsDiff()
                .diff(left.getExtensions(), right.getExtensions(), context))
        .ifPresent(changedParameter::setExtensions);
    return builder.buildIsChanged(changedParameter);
  }
}
