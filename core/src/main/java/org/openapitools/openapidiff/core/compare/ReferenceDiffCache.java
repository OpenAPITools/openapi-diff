package org.openapitools.openapidiff.core.compare;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;
import org.openapitools.openapidiff.core.model.deferred.RealizedChanged;

/** Created by adarsh.sharma on 07/01/18. */
public abstract class ReferenceDiffCache<C, D extends Changed> {
  private final Map<CacheKey, DeferredChanged<D>> refDiffMap;

  public ReferenceDiffCache() {
    this.refDiffMap = new HashMap<>();
  }

  private DeferredChanged<D> getFromCache(CacheKey cacheKey) {
    return refDiffMap.get(cacheKey);
  }

  private void addToCache(CacheKey cacheKey, DeferredChanged<D> changed) {
    refDiffMap.put(cacheKey, changed);
  }

  public DeferredChanged<D> cachedDiff(
      HashSet<String> refSet,
      C left,
      C right,
      String leftRef,
      String rightRef,
      DiffContext context) {
    boolean areBothRefParameters = leftRef != null && rightRef != null;
    if (areBothRefParameters) {
      CacheKey key = new CacheKey(leftRef, rightRef, context);
      DeferredChanged<D> changedFromRef = getFromCache(key);
      if (changedFromRef != null) {
        return changedFromRef;
      } else {
        String refKey = getRefKey(leftRef, rightRef);
        if (refSet.contains(refKey)) {
          return RealizedChanged.empty();
        } else {
          refSet.add(refKey);
          DeferredChanged<D> changed = computeDiff(refSet, left, right, context);
          addToCache(key, changed);
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

  protected abstract DeferredChanged<D> computeDiff(
      HashSet<String> refSet, C left, C right, DiffContext context);
}
