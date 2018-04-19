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
    private final ApiResponses oldApiResponses;
    private final ApiResponses newApiResponses;
    private final DiffContext context;
    private Map<String, ApiResponse> missingResponses;
    private Map<String, ApiResponse> addResponses;
    private Map<String, ChangedResponse> changedResponses;

    public ChangedApiResponse(ApiResponses oldApiResponses, ApiResponses newApiResponses, DiffContext context) {
        this.oldApiResponses = oldApiResponses;
        this.newApiResponses = newApiResponses;
        this.context = context;
        this.missingResponses = new HashMap<>();
        this.addResponses = new HashMap<>();
        this.changedResponses = new HashMap<>();
    }

    @Override
    public DiffResult isChanged() {
        if (addResponses.size() == 0 && missingResponses.size() == 0 && changedResponses.size() == 0) {
            return DiffResult.NO_CHANGES;
        }
        if (missingResponses.size() == 0 && changedResponses.values().stream().allMatch(Changed::isCompatible)) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }
}
