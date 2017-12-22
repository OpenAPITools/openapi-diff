package com.qdesrame.openapi.diff.model;

import io.swagger.oas.models.Operation;
import io.swagger.oas.models.PathItem;

import java.util.Map;


public class ChangedEndpoint implements Changed {

    private String pathUrl;
    private PathItem oldPathItem;
    private PathItem newPathItem;

    private Map<PathItem.HttpMethod, Operation> newOperations;
    private Map<PathItem.HttpMethod, Operation> missingOperations;
    private Map<PathItem.HttpMethod, Operation> deprecatedOperations;
    private Map<PathItem.HttpMethod, ChangedOperation> changedOperations;

    public ChangedEndpoint(String pathUrl, PathItem oldPathItem, PathItem newPathItem) {
        this.pathUrl = pathUrl;
        this.oldPathItem = oldPathItem;
        this.newPathItem = newPathItem;
    }

    public String getPathUrl() {
        return pathUrl;
    }

    public PathItem getOldPathItem() {
        return oldPathItem;
    }

    public PathItem getNewPathItem() {
        return newPathItem;
    }

    public Map<PathItem.HttpMethod, Operation> getNewOperations() {
        return newOperations;
    }

    public void setNewOperations(Map<PathItem.HttpMethod, Operation> newOperations) {
        this.newOperations = newOperations;
    }

    public Map<PathItem.HttpMethod, Operation> getMissingOperations() {
        return missingOperations;
    }

    public void setMissingOperations(
            Map<PathItem.HttpMethod, Operation> missingOperations) {
        this.missingOperations = missingOperations;
    }


    public Map<PathItem.HttpMethod, ChangedOperation> getChangedOperations() {
        return changedOperations;
    }

    public void setChangedOperations(
            Map<PathItem.HttpMethod, ChangedOperation> changedOperations) {
        this.changedOperations = changedOperations;
    }

    public boolean isDiff() {
//		newOperations.isEmpty() 
//		|| !missingOperations.isEmpty()
//		|| 
        return !changedOperations.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("Changed endpoint '%s': %d missing, %d changed", pathUrl, getMissingOperations().size(), getChangedOperations().size());
    }

    public void setDeprecatedOperations(Map<PathItem.HttpMethod,Operation> deprecatedOperations) {
        this.deprecatedOperations = deprecatedOperations;
    }

    public Map<PathItem.HttpMethod,Operation> getDeprecatedOperations() {
        return deprecatedOperations;
    }

    public boolean isDeprecated() {
        return !deprecatedOperations.isEmpty();
    }
}
