package org.openapitools.openapidiff.core.compare;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedPaths;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredBuilder;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;

public class PathsDiff {
  private static final String REGEX_PATH = "\\{([^/]+)}";
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
                            if (methodsIntersect(a.getValue(), b.getValue())) {
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
                DiffContext context = new DiffContext();
                context.setUrl(url);
                context.setParameters(params);
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

  private static boolean methodsIntersect(PathItem a, PathItem b) {
    Set<PathItem.HttpMethod> methodsA = a.readOperationsMap().keySet();
    for (PathItem.HttpMethod method : b.readOperationsMap().keySet()) {
      if (methodsA.contains(method)) {
        return true;
      }
    }
    return false;
  }
}
