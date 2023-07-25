package org.openapitools.openapidiff.core.compare;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedParameters;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredBuilder;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;
import org.openapitools.openapidiff.core.utils.RefPointer;
import org.openapitools.openapidiff.core.utils.RefType;

class ParametersDiffResult {
  protected DeferredChanged<ChangedParameters> deferredChanged;
  protected boolean sameOperationsDiffSchema;

  public ParametersDiffResult(
      DeferredChanged<ChangedParameters> deferredChanged, boolean sameOperationsDiffSchema) {
    this.deferredChanged = deferredChanged;
    this.sameOperationsDiffSchema = sameOperationsDiffSchema;
  }
}
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

  public ParametersDiffResult diff(
      final List<Parameter> left, final List<Parameter> right, final DiffContext context) {
    final DeferredBuilder<Changed> builder = new DeferredBuilder<>();
    final List<Parameter> wLeft = Optional.ofNullable(left).orElseGet(Collections::emptyList);
    final List<Parameter> wRight =
        Optional.ofNullable(right).map(ArrayList::new).orElseGet(ArrayList::new);

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
    return new ParametersDiffResult(
        builder.buildIsChanged(changedParameters),
        pathUnchangedParametersChanged(changedParameters, context));
  }

  public boolean pathUnchangedParametersChanged(
      ChangedParameters changedParameters, DiffContext context) {
    if (!pathUnchanged(changedParameters, context)) return false;
    // If missing and increased parameter count is different, it's a new endpoint
    if (changedParameters.getMissing().size() != changedParameters.getIncreased().size())
      return false;
    // Go through each missing Parameter and compare it to newly added Parameters
    for (Parameter parameter : changedParameters.getMissing()) {
      // Speedy Check. Use the map already created in changedParameters to check if missing param is
      // linked to newParam
      String newParameterName = context.getParameters().get(parameter.getName());
      if (StringUtils.isBlank(newParameterName)) return false;

      Optional<Parameter> newParameter =
          changedParameters.getIncreased().stream()
              .filter(p -> p.getName().equals(newParameterName))
              .findFirst();
      if (!newParameter.isPresent()) return false;

      // Check if  the old and new Parameters match . IF so, return TRUE
      Parameter newParameterRealized = newParameter.get();
      newParameterRealized.setName(parameter.getName()); // Make names similar
      boolean samePathDifferentParameter = !newParameterRealized.equals(parameter);
      newParameterRealized.setName(
          newParameterName); // Important:: MUST Reset the name as this is not a copy
      return samePathDifferentParameter;
    }
    return false;
  }

  public boolean pathUnchanged(ChangedParameters changedParameters, DiffContext context) {
    final String REGEX_PATH = "\\{([^/]+)}";
    String oldUrl = context.getLeftUrl();
    String newUrl = context.getRightUrl();
    ArrayList<String> oldUrlPathParams = matchedItems(oldUrl, REGEX_PATH);
    ArrayList<String> newUrlPathParams = matchedItems(newUrl, REGEX_PATH);
    // Path Param count doesn't match or param-less path doesn't match or param is changed --> It's
    // a new endpoint
    return oldUrlPathParams.size() == newUrlPathParams.size()
        && changedParameters.getChanged().isEmpty()
        && oldUrl.replaceAll(REGEX_PATH, "").equals(newUrl.replaceAll(REGEX_PATH, ""));
  }

  public ArrayList<String> matchedItems(String string, String regex) {
    Matcher matcher = Pattern.compile(regex).matcher(string);
    ArrayList<String> matchedItems = new ArrayList<>();
    while (matcher.find()) {
      String item = matcher.group();
      matchedItems.add(item.substring(0, matcher.group().length() - 1).replaceFirst("\\{", ""));
    }
    return matchedItems;
  }
}
