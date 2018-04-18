package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.DiffContext;

import java.util.HashMap;
import java.util.HashSet;
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

    //    public Optional<D> cachedDiff(HashSet<String> refSet, C left, C right, String leftRef, String rightRef) {
//        return cachedDiff(refSet, left, right, leftRef, rightRef, null);
//    }
//
    public Optional<D> cachedDiff(HashSet<String> refSet, C left, C right, String leftRef, String rightRef, DiffContext context) {
        boolean areBothRefParameters = leftRef != null && rightRef != null;
        if (areBothRefParameters) {
            Optional<D> changedFromRef = getFromCache(leftRef, rightRef);
            if (changedFromRef.isPresent()) {
                return changedFromRef;
            } else {
                String refKey = getRefKey(leftRef, rightRef);
                if (refSet.contains(refKey)) {
                    return Optional.empty();
                } else {
                    refSet.add(refKey);
                    Optional<D> changed = computeDiff(refSet, left, right, context);
                    addToCache(leftRef, rightRef, changed.orElse(null));
                    refSet.remove(refKey);

                    return changed;
                }
            }
        } else {
            return computeDiff(refSet, left, right, context);
        }
    }

    protected String getRefKey(String leftRef, String rightRef) {
        return leftRef + ":" + rightRef;
    }

    protected abstract Optional<D> computeDiff(HashSet<String> refSet, C left, C right, DiffContext context);

}
