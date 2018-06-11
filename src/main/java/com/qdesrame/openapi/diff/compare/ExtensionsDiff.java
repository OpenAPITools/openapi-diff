package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.DiffContext;
import com.qdesrame.openapi.diff.model.schema.ChangedExtensions;

import java.util.*;

import static com.qdesrame.openapi.diff.utils.ChangedUtils.isChanged;

public class ExtensionsDiff {
    private final OpenApiDiff openApiDiff;

    private ServiceLoader<ExtensionDiff> extensionsLoader = ServiceLoader.load(ExtensionDiff.class);
    private List<ExtensionDiff> extensionsDiff = new ArrayList<>();

    public ExtensionsDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.extensionsLoader.reload();
        for (ExtensionDiff anExtensionsLoader : this.extensionsLoader) {
            extensionsDiff.add(anExtensionsLoader);
        }
    }

    public Optional<ChangedExtensions> diff(Map<String, Object> left, Map<String, Object> right, DiffContext context) {
        if (null == left) left = new LinkedHashMap<>();
        if (null == right) right = new LinkedHashMap<>();
        ChangedExtensions changedExtensions = new ChangedExtensions(left, new LinkedHashMap<>(right), context);
        changedExtensions.getIncreased().putAll(right);
        for (String key : left.keySet()) {
            if (changedExtensions.getIncreased().containsKey(key)) {
                Optional<ExtensionDiff> extensionDiff = extensionsDiff.stream()
                        .filter(diff -> ("x-" + diff.getName()).equals(key)).findFirst();
                Object leftValue = left.get(key);
                Object rightValue = changedExtensions.getIncreased().remove(key);
                extensionDiff.ifPresent(diff -> diff.setOpenApiDiff(openApiDiff).diff(leftValue, rightValue, context)
                        .ifPresent(changed -> changedExtensions.getChanged().put(key, changed)));
            } else {
                changedExtensions.getMissing().put(key, left.get(key));
            }
        }
        return isChanged(changedExtensions);
    }
}
