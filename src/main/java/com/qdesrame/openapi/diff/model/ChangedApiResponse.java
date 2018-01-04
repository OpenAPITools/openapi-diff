package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adarsh.sharma on 22/12/17.
 */
@Getter
@Setter
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
}
