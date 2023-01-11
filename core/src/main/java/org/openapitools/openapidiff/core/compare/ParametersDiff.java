package org.openapitools.openapidiff.core.compare;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedParameters;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredBuilder;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;
import org.openapitools.openapidiff.core.utils.RefPointer;
import org.openapitools.openapidiff.core.utils.RefType;

/** compare two parameter */
public class ParametersDiff {

  private static final RefPointer<Parameter> refPointer = new RefPointer<>(RefType.PARAMETERS);

  private final Components leftComponents;
  private final Components rightComponents;
  private final OpenApiDiff openApiDiff;

  public ParametersDiff(OpenApiDiff openApiDiff) {
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

  public static Optional<Parameter> contains(
      Components components, List<Parameter> parameters, Parameter parameter) {
    return parameters.stream()
        .filter(param -> same(refPointer.resolveRef(components, param, param.get$ref()), parameter))
        .findFirst();
  }

  public static boolean same(Parameter left, Parameter right) {
    return Objects.equals(left.getName(), right.getName())
        && Objects.equals(left.getIn(), right.getIn());
  }

  public DeferredChanged<ChangedParameters> diff(
      final List<Parameter> left, final List<Parameter> right, final DiffContext context) {
    final DeferredBuilder<Changed> builder = new DeferredBuilder<>();
    final List<Parameter> wLeft = Optional.ofNullable(left).orElseGet(Collections::emptyList);
    final List<Parameter> wRight = Optional.ofNullable(right).map(ArrayList::new).orElseGet(ArrayList::new);

    final ChangedParameters changedParameters = new ChangedParameters(wLeft, wRight, context);

    for (Parameter leftParam : wLeft) {
      leftParam = refPointer.resolveRef(leftComponents, leftParam, leftParam.get$ref());
      Optional<Parameter> rightParamOpt = contains(rightComponents, wRight, leftParam);
      if (!rightParamOpt.isPresent()) {
        changedParameters.getMissing().add(leftParam);
      } else {
        Parameter rightParam = rightParamOpt.get();
        wRight.remove(rightParam);
        builder
            .with(openApiDiff.getParameterDiff().diff(leftParam, rightParam, context))
            .ifPresent(changedParameters.getChanged()::add);
      }
    }
    changedParameters.getIncreased().addAll(wRight);

    return builder.buildIsChanged(changedParameters);
  }
}
