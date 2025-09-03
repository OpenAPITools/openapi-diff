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
  private static final String REGEX_PATH = "\\{([^/{}]+)}";
  private final OpenApiDiff openApiDiff;

  public PathsDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  public DeferredChanged<ChangedPaths> diff(
      final Map<String, PathItem> left, final Map<String, PathItem> right) {
    DeferredBuilder<Changed> builder = new DeferredBuilder<>();

    ChangedPaths changedPaths = new ChangedPaths(left, right, openApiDiff.getOptions());
    changedPaths.getIncreased().putAll(right);

    left.entrySet()
        .forEach(
            pathEntry -> {
              String leftUrl = pathEntry.getKey();
              PathItem leftPath = pathEntry.getValue();
              Optional<Map.Entry<String, PathItem>> result =
                  openApiDiff
                      .getOptions()
                      .getPathMatcher()
                      .find(pathEntry, changedPaths.getIncreased());
              if (result.isPresent()) {
                String rightUrl = result.get().getKey();
                PathItem rightPath = changedPaths.getIncreased().remove(rightUrl);
                Map<String, String> params = new LinkedHashMap<>();
                if (!leftUrl.equals(rightUrl)) {
                  List<String> oldParams = extractParameters(leftUrl);
                  List<String> newParams = extractParameters(rightUrl);
                  for (int i = 0; i < oldParams.size(); i++) {
                    params.put(oldParams.get(i), newParams.get(i));
                  }
                }
                DiffContext context = new DiffContext(openApiDiff.getOptions());
                context.setUrl(leftUrl);
                context.setParameters(params);
                context.setLeftAndRightUrls(leftUrl, rightUrl);
                builder
                    .with(openApiDiff.getPathDiff().diff(leftPath, rightPath, context))
                    .ifPresent(path -> changedPaths.getChanged().put(rightUrl, path));
              } else {
                changedPaths.getMissing().put(leftUrl, leftPath);
              }
            });
    return builder.buildIsChanged(changedPaths);
  }

  private List<String> extractParameters(String path) {
    ArrayList<String> params = new ArrayList<>();
    Pattern pattern = Pattern.compile(REGEX_PATH);
    Matcher matcher = pattern.matcher(path);
    while (matcher.find()) {
      params.add(matcher.group(1));
    }
    return params;
  }

  public static Paths valOrEmpty(Paths path) {
    if (path == null) {
      path = new Paths();
    }
    return path;
  }
}
