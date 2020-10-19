package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;
import static org.openapitools.openapidiff.core.utils.Copy.copyMap;

import java.util.*;
import java.util.function.Function;
import org.openapitools.openapidiff.core.model.Change;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedExtensions;
import org.openapitools.openapidiff.core.model.DiffContext;

public class ExtensionsDiff {
  private final OpenApiDiff openApiDiff;

  private final List<ExtensionDiff> extensionDiffs = new ArrayList<>();

  public ExtensionsDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
    ServiceLoader<ExtensionDiff> extensionsLoader = ServiceLoader.load(ExtensionDiff.class);
    extensionsLoader.reload();
    for (ExtensionDiff anExtensionsLoader : extensionsLoader) {
      extensionDiffs.add(anExtensionsLoader);
    }
  }

  public boolean isParentApplicable(
      Change.Type type, Object parent, Map<String, Object> extensions, DiffContext context) {
    if (extensions.isEmpty()) {
      return true;
    }
    return extensions.entrySet().stream()
        .map(
            entry ->
                executeExtension(
                    entry.getKey(),
                    extensionDiff ->
                        extensionDiff.isParentApplicable(type, parent, entry.getValue(), context)))
        .allMatch(aBoolean -> aBoolean.orElse(true));
  }

  public Optional<ExtensionDiff> getExtensionDiff(String name) {
    return extensionDiffs.stream().filter(diff -> ("x-" + diff.getName()).equals(name)).findFirst();
  }

  public <T> Optional<T> executeExtension(String name, Function<ExtensionDiff, T> predicate) {
    return getExtensionDiff(name)
        .map(extensionDiff -> extensionDiff.setOpenApiDiff(openApiDiff))
        .map(predicate);
  }

  public Optional<ChangedExtensions> diff(Map<String, Object> left, Map<String, Object> right) {
    return this.diff(left, right, null);
  }

  public Optional<ChangedExtensions> diff(
      Map<String, Object> left, Map<String, Object> right, DiffContext context) {
    left = copyMap(left);
    right = copyMap(right);
    ChangedExtensions changedExtensions = new ChangedExtensions(left, copyMap(right), context);
    for (String key : left.keySet()) {
      Object leftValue = left.get(key);
      if (right.containsKey(key)) {
        Object rightValue = right.remove(key);
        executeExtensionDiff(key, Change.changed(leftValue, rightValue), context)
            .filter(Changed::isDifferent)
            .ifPresent(changed -> changedExtensions.getChanged().put(key, changed));
      } else {
        executeExtensionDiff(key, Change.removed(leftValue), context)
            .filter(Changed::isDifferent)
            .ifPresent(changed -> changedExtensions.getMissing().put(key, changed));
      }
    }
    right.forEach(
        (key, value) ->
            executeExtensionDiff(key, Change.added(value), context)
                .filter(Changed::isDifferent)
                .ifPresent(changed -> changedExtensions.getIncreased().put(key, changed)));
    return isChanged(changedExtensions);
  }

  private Optional<Changed> executeExtensionDiff(String name, Change change, DiffContext context) {
    return executeExtension(name, diff -> diff.setOpenApiDiff(openApiDiff).diff(change, context));
  }
}
