package com.qdesrame.openapi.diff.compare;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by adarsh.sharma on 07/01/18.
 */
public abstract class ReferenceDiffCache<C, D> {
    private Map<String, Map<String, D>> refDiffMap;

    public ReferenceDiffCache() {
        this.refDiffMap = new HashMap<>();
    }

    private Optional<D> getFromCache(String leftRef, String rightRef) {
        Optional<Map<String, D>> changedSchemaMap = Optional.ofNullable(refDiffMap.get(leftRef));
        if (changedSchemaMap.isPresent()) {
            return Optional.ofNullable(changedSchemaMap.get().get(rightRef));
        }
        return Optional.empty();
    }

    private void addToCache(String leftRef, String rightRef, D changed) {
        Map<String, D> changedSchemaMap = refDiffMap.computeIfAbsent(leftRef, k -> new HashMap<>());
        changedSchemaMap.put(rightRef, changed);
    }

    public Optional<D> cachedDiff(C left, C right, String leftRef, String rightRef) {
        boolean areBothRefParameters = leftRef != null && rightRef != null;
        if (areBothRefParameters) {
            Optional<D> changedFromRef = getFromCache(leftRef, rightRef);
            if (changedFromRef.isPresent()) {
                return changedFromRef;
            }
        }

        Optional<D> changed = computeDiff(left, right);

        if(areBothRefParameters) {
            addToCache(leftRef, rightRef, changed.isPresent()? changed.get(): null);
        }

        return changed;
    }

    protected abstract Optional<D> computeDiff(C left, C right);

}
