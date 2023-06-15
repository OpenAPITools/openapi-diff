package org.openapitools.openapidiff.core.compare;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedPaths;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredBuilder;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;

public class PathsDiff {
  private static final String REGEX_PATH = "\\{([^/{}]+)}";
  private final OpenApiDiff openApiDiff;

  public PathsDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  private static String normalizePath(String path) {
    return path.replaceAll(REGEX_PATH, "{}");
  }

  private static List<String> extractParameters(String path) {
    ArrayList<String> params = new ArrayList<>();
    Pattern pattern = Pattern.compile(REGEX_PATH);
    Matcher matcher = pattern.matcher(path);
    while (matcher.find()) {
      params.add(matcher.group(1));
    }
    return params;
  }

  public DeferredChanged<ChangedPaths> diff(
      final Map<String, PathItem> left, final Map<String, PathItem> right) {
    DeferredBuilder<Changed> builder = new DeferredBuilder<>();

    ChangedPaths changedPaths = new ChangedPaths(left, right);
    changedPaths.getIncreased().putAll(right);

    left.keySet()
        .forEach(
            (String url) -> {
              PathItem leftPath = left.get(url);
              String template = normalizePath(url);
              Optional<Map.Entry<String, PathItem>> result =
                  changedPaths.getIncreased().entrySet().stream()
                      .filter(item -> normalizePath(item.getKey()).equals(template))
                      .min(
                          (a, b) -> {
                            if (methodsAndParametersIntersect(a.getValue(), b.getValue())) {
                              throw new IllegalArgumentException(
                                  "Two path items have the same signature: " + template);
                            }
                            if (a.getKey().equals(url)) {
                              return -1;
                            } else if (b.getKey().equals((url))) {
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
              if (result.isPresent()) {
                String rightUrl = result.get().getKey();
                PathItem rightPath = changedPaths.getIncreased().remove(rightUrl);
                Map<String, String> params = new LinkedHashMap<>();
                if (!url.equals(rightUrl)) {
                  List<String> oldParams = extractParameters(url);
                  List<String> newParams = extractParameters(rightUrl);
                  for (int i = 0; i < oldParams.size(); i++) {
                    params.put(oldParams.get(i), newParams.get(i));
                  }
                }
                DiffContext context = new DiffContext(openApiDiff.getOptions());
                context.setUrl(url);
                context.setParameters(params);
                context.setLeftAndRightUrls(url, rightUrl);
                builder
                    .with(openApiDiff.getPathDiff().diff(leftPath, rightPath, context))
                    .ifPresent(path -> changedPaths.getChanged().put(rightUrl, path));
              } else {
                changedPaths.getMissing().put(url, leftPath);
              }
            });
    return builder.buildIsChanged(changedPaths);
  }

  public static Paths valOrEmpty(Paths path) {
    if (path == null) {
      path = new Paths();
    }
    return path;
  }

  /**
   * @param a a path form the open api spec
   * @param b another path from the same open api spec
   * @return <code>true</code> in case both paths are of the same method AND their templated
   *     parameters are of the same type; <code>false</code> otherwise
   */
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

  /**
   * @param left parameters from the first compared method
   * @param right parameters from the second compared method
   * @return <code>true</code> in case each parameter pair is of the same type; <code>false</code>
   *     otherwise
   */
  private static boolean parametersIntersect(List<Parameter> left, List<Parameter> right) {
    int parametersSize = left.size();
    long intersectedParameters =
        IntStream.range(0, left.size())
            .filter(
                i -> left.get(i).getSchema().getType().equals(right.get(i).getSchema().getType()))
            .count();
    return parametersSize == intersectedParameters;
  }
}
