package com.qdesrame.openapi.diff.compare;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adarsh.sharma on 25/12/17.
 */
public class ReferenceDiffCache<T> {
    private Map<String, Map<String, T>> schemaDiffMap;

    public ReferenceDiffCache() {
        this.schemaDiffMap = new HashMap<>();
    }

    public T getFromCache(String leftRef, String rightRef) {
        Map<String, T> changedSchemaMap = schemaDiffMap.get(leftRef);
        if (changedSchemaMap != null) {
            return changedSchemaMap.get(rightRef);
        }
        return null;
    }

    public void addToCache(String leftRef, String rightRef, T changed) {
        Map<String, T> changedSchemaMap = schemaDiffMap.get(leftRef);
        if (changedSchemaMap == null) {
            changedSchemaMap = new HashMap<>();
            schemaDiffMap.put(leftRef, changedSchemaMap);
        }
        changedSchemaMap.put(rightRef, changed);
    }
}
