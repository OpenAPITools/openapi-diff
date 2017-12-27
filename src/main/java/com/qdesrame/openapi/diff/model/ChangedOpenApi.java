package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.utils.EndpointUtils;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adarsh.sharma on 22/12/17.
 */
public class ChangedOpenApi implements Changed {
    private OpenAPI oldSpecOpenApi;
    private OpenAPI newSpecOpenApi;

    private List<Endpoint> newEndpoints;
    private List<Endpoint> missingEndpoints;
    private List<ChangedOperation> changedOperations;

    public OpenAPI getOldSpecOpenApi() {
        return oldSpecOpenApi;
    }

    public void setOldSpecOpenApi(OpenAPI oldSpecOpenApi) {
        this.oldSpecOpenApi = oldSpecOpenApi;
    }

    public OpenAPI getNewSpecOpenApi() {
        return newSpecOpenApi;
    }

    public void setNewSpecOpenApi(OpenAPI newSpecOpenApi) {
        this.newSpecOpenApi = newSpecOpenApi;
    }

    public List<Endpoint> getNewEndpoints() {
        return newEndpoints;
    }

    public void setNewEndpoints(List<Endpoint> newEndpoints) {
        this.newEndpoints = newEndpoints;
    }

    public List<Endpoint> getMissingEndpoints() {
        return missingEndpoints;
    }

    public void setMissingEndpoints(List<Endpoint> missingEndpoints) {
        this.missingEndpoints = missingEndpoints;
    }

    public List<Endpoint> getDeprecatedEndpoints() {
        return changedOperations.stream()
                .filter(c -> c.isDeprecated())
                .map(c -> EndpointUtils.convert2Endpoint(c.getPathUrl(), c.getHttpMethod(), c.getNewOperation()))
                .collect(Collectors.toList());
    }

    public List<ChangedOperation> getChangedOperations() {
        return changedOperations;
    }

    public void setChangedOperations(List<ChangedOperation> changedOperations) {
        this.changedOperations = changedOperations;
    }

    @Override
    public boolean isDiff() {
        return newEndpoints.size() > 0
                || missingEndpoints.size() > 0
                || changedOperations.size() > 0;
    }

    public boolean isDiffBackwardCompatible() {
        return missingEndpoints.size() == 0
                && changedOperations.stream().allMatch(c -> c.isDiffBackwardCompatible());
    }

}
