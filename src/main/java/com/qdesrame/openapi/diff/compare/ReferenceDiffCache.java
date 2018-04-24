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
    private Map<CacheKey, D> refDiffMap;

    public ReferenceDiffCache() {
        this.refDiffMap = new HashMap<>();
    }

    private Optional<D> getFromCache(CacheKey cacheKey) {
        return Optional.ofNullable(refDiffMap.get(cacheKey));
    }

    private void addToCache(CacheKey cacheKey, D changed) {
        refDiffMap.put(cacheKey, changed);
    }

    public Optional<D> cachedDiff(HashSet<String> refSet, C left, C right, String leftRef, String rightRef, DiffContext context) {
        boolean areBothRefParameters = leftRef != null && rightRef != null;
        if (areBothRefParameters) {
            CacheKey key = new CacheKey(leftRef, rightRef, context);
            Optional<D> changedFromRef = getFromCache(key);
            if (changedFromRef.isPresent()) {
                return changedFromRef;
            } else {
                String refKey = getRefKey(leftRef, rightRef);
                if (refSet.contains(refKey)) {
                    return Optional.empty();
                } else {
                    refSet.add(refKey);
                    Optional<D> changed = computeDiff(refSet, left, right, context);
                    addToCache(key, changed.orElse(null));
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
