package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedPaths;
import io.swagger.v3.oas.models.PathItem;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathsDiff {
    private static final String REGEX_PATH = "\\{([^/]+)\\}";
    private OpenApiDiff openApiDiff;

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

    public Optional<ChangedPaths> diff(final Map<String, PathItem> left, final Map<String, PathItem> right) {
        ChangedPaths changedPaths = new ChangedPaths(left, right);
        changedPaths.getIncreased().putAll(right);
        left.keySet().forEach((String url) -> {
            PathItem leftPath = left.get(url);
            String template = normalizePath(url);
            Optional<String> result = right.keySet().stream().filter(s -> normalizePath(s).equals(template)).findFirst();
            if (result.isPresent()) {
                PathItem rightPath = changedPaths.getIncreased().remove(result.get());
                Map<String, String> params = new HashMap<>();
                if (!url.equals(result.get())) {
                    List<String> oldParams = extractParameters(url);
                    List<String> newParams = extractParameters(result.get());
                    for (int i = 0; i < oldParams.size(); i++) {
                        params.put(oldParams.get(i), newParams.get(i));
                    }
                }
                openApiDiff.getPathDiff().diff(url, params, leftPath, rightPath).ifPresent(path -> changedPaths.getChanged().put(result.get(), path));
            } else {
                changedPaths.getMissing().put(url, leftPath);
            }
        });
        return changedPaths.isDiff() ? Optional.of(changedPaths) : Optional.empty();
    }
}
