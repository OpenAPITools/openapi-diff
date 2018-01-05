package com.qdesrame.openapi.diff.compare;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by adarsh.sharma on 25/12/17.
 */
public class ReferenceDiffCache<T> {
    private Map<String, Map<String, T>> schemaDiffMap;

    public ReferenceDiffCache() {
        this.schemaDiffMap = new HashMap<>();
    }

    public Optional<T> getFromCache(String leftRef, String rightRef) {
        Optional<Map<String, T>> changedSchemaMap = Optional.ofNullable(schemaDiffMap.get(leftRef));
        if (changedSchemaMap.isPresent()) {
            return Optional.ofNullable(changedSchemaMap.get().get(rightRef));
        }
        return Optional.empty();
    }

    public void addToCache(String leftRef, String rightRef, T changed) {
        Map<String, T> changedSchemaMap = schemaDiffMap.computeIfAbsent(leftRef, k -> new HashMap<>());
        changedSchemaMap.put(rightRef, changed);
    }
}
