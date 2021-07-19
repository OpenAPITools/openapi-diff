package org.openapitools.openapidiff.core.compare;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.ArrayList;
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

/**
 * compare two parameter
 *
 * @author QDesrame
 */
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
      List<Parameter> left, List<Parameter> right, DiffContext context) {

    DeferredBuilder<Changed> builder = new DeferredBuilder<Changed>();
    ChangedParameters changedParameters =
        new ChangedParameters(left, right != null ? new ArrayList<>(right) : null, context);
    if (null == left) left = new ArrayList<>();
    if (null == right) right = new ArrayList<>();

    for (Parameter leftPara : left) {
      leftPara = refPointer.resolveRef(leftComponents, leftPara, leftPara.get$ref());

      Optional<Parameter> rightParam = contains(rightComponents, right, leftPara);
      if (!rightParam.isPresent()) {
        changedParameters.getMissing().add(leftPara);
      } else {
        Parameter rightPara = rightParam.get();
        right.remove(rightPara);
        builder
            .with(openApiDiff.getParameterDiff().diff(leftPara, rightPara, context))
            .ifPresent(changedParameters.getChanged()::add);
      }
    }
    changedParameters.getIncreased().addAll(right);

    return builder.buildIsChanged(changedParameters);
  }
}
