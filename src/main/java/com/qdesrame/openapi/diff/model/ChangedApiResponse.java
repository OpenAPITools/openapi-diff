package com.qdesrame.openapi.diff.model;

import io.swagger.oas.models.responses.ApiResponse;
import io.swagger.oas.models.responses.ApiResponses;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adarsh.sharma on 22/12/17.
 */
public class ChangedApiResponse implements Changed {
    private ApiResponses oldApiResponses;
    private ApiResponses newApiResponses;
    private Map<String, ApiResponse> missingResponses;
    private Map<String, ApiResponse> addResponses;
    private Map<String, ChangedResponse> changedResponses;

    public ChangedApiResponse(ApiResponses oldApiResponses, ApiResponses newApiResponses) {
        this.oldApiResponses = oldApiResponses;
        this.newApiResponses = newApiResponses;
        this.missingResponses = new HashMap<>();
        this.addResponses = new HashMap<>();
        this.changedResponses = new HashMap<>();
    }

    @Override
    public boolean isDiff() {
        return !addResponses.isEmpty() || !missingResponses.isEmpty() || !changedResponses.isEmpty();
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return addResponses.size() == 0
                && changedResponses.values().stream().allMatch(c -> c.isDiffBackwardCompatible());
    }

    public ApiResponses getOldApiResponses() {
        return oldApiResponses;
    }

    public ApiResponses getNewApiResponses() {
        return newApiResponses;
    }

    public Map<String, ApiResponse> getMissingResponses() {
        return missingResponses;
    }

    public void setMissingResponses(Map<String, ApiResponse> missingResponses) {
        this.missingResponses = missingResponses;
    }

    public Map<String, ApiResponse> getAddResponses() {
        return addResponses;
    }

    public void setAddResponses(Map<String, ApiResponse> addResponses) {
        this.addResponses = addResponses;
    }

    public Map<String, ChangedResponse> getChangedResponses() {
        return changedResponses;
    }

    public void setChangedResponses(Map<String, ChangedResponse> changedResponses) {
        this.changedResponses = changedResponses;
    }
}
