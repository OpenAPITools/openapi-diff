package org.openapitools.openapidiff.core.compare;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
      List<Parameter> left, List<Parameter> right, DiffContext context) {
    DeferredBuilder<Changed> builder = new DeferredBuilder<>();
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
      if (newParameterName.isEmpty()) return false;

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
