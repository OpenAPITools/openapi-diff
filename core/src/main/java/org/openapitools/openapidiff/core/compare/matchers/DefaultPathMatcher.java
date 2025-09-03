package org.openapitools.openapidiff.core.compare.matchers;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

/** Default implementation of PathMatcher */
public class DefaultPathMatcher implements PathMatcher {

  private static final String REGEX_PATH = "\\{([^/{}]+)}";

  @Override
  public Optional<Map.Entry<String, PathItem>> find(
      Map.Entry<String, PathItem> what, Map<String, PathItem> candidates) {
    String leftUrl = what.getKey();
    PathItem leftPath = what.getValue();

    final String template = normalizePath(leftUrl);
    return candidates.entrySet().stream()
        .filter(item -> normalizePath(item.getKey()).equals(template))
        .min(
            (a, b) -> {
              if (methodsAndParametersIntersect(a.getValue(), b.getValue())) {
                throw new IllegalArgumentException(
                    "Two path items have the same signature: " + template);
              }
              if (a.getKey().equals(leftUrl)) {
                return -1;
              } else if (b.getKey().equals(leftUrl)) {
                return 1;
              } else {
                HashSet<PathItem.HttpMethod> methodsA =
                    new HashSet<>(a.getValue().readOperationsMap().keySet());
                methodsA.retainAll(leftPath.readOperationsMap().keySet());
                HashSet<PathItem.HttpMethod> methodsB =
                    new HashSet<>(b.getValue().readOperationsMap().keySet());
                methodsB.retainAll(leftPath.readOperationsMap().keySet());
                return Integer.compare(methodsB.size(), methodsA.size());
              }
            });
  }

  private static String normalizePath(String path) {
    return path.replaceAll(REGEX_PATH, "{}");
  }

  private static boolean methodsAndParametersIntersect(PathItem a, PathItem b) {
    Set<PathItem.HttpMethod> methodsA = a.readOperationsMap().keySet();
    for (PathItem.HttpMethod method : b.readOperationsMap().keySet()) {
      if (methodsA.contains(method)) {
        Operation left = a.readOperationsMap().get(method);
        Operation right = b.readOperationsMap().get(method);
        if (left.getParameters().size() == right.getParameters().size()) {
          return parametersIntersect(left.getParameters(), right.getParameters());
        }
        return false;
      }
    }
    return false;
  }

  private static boolean parametersIntersect(List<Parameter> left, List<Parameter> right) {
    int parametersSize = left.size();
    long intersectedParameters =
        IntStream.range(0, left.size())
            .filter(i -> parametersTypeEquals(left.get(i), right.get(i)))
            .count();
    return parametersSize == intersectedParameters;
  }

  private static boolean parametersTypeEquals(Parameter left, Parameter right) {
    return Objects.equals(left.getSchema().getType(), right.getSchema().getType())
        && Objects.equals(left.getSchema().getFormat(), right.getSchema().getFormat());
  }
}
