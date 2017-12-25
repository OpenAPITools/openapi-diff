package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedSchema;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adarsh.sharma on 25/12/17.
 */
public class SchemaDiffCache {
    private Map<String, Map<String, ChangedSchema>> schemaDiffMap;

    public SchemaDiffCache() {
        this.schemaDiffMap = new HashMap<>();
    }

    public ChangedSchema getFromCache(String leftRef, String rightRef) {
        Map<String, ChangedSchema> changedSchemaMap = schemaDiffMap.get(leftRef);
        if (changedSchemaMap != null) {
            return changedSchemaMap.get(rightRef);
        }
        return null;
    }

    public void addToCache(String leftRef, String rightRef, ChangedSchema changedSchema) {
        Map<String, ChangedSchema> changedSchemaMap = schemaDiffMap.get(leftRef);
        if (changedSchemaMap == null) {
            changedSchemaMap = new HashMap<>();
            schemaDiffMap.put(leftRef, changedSchemaMap);
        }
        changedSchemaMap.put(rightRef, changedSchema);
    }
}
