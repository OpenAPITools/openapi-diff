package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.utils.EndpointUtils;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adarsh.sharma on 22/12/17.
 */
@Getter
@Setter
public class ChangedOpenApi implements Changed {
    private OpenAPI oldSpecOpenApi;
    private OpenAPI newSpecOpenApi;

    private List<Endpoint> newEndpoints;
    private List<Endpoint> missingEndpoints;
    private List<ChangedOperation> changedOperations;

    public List<Endpoint> getDeprecatedEndpoints() {
        return changedOperations.stream()
                .filter(c -> c.isDeprecated())
                .map(c -> EndpointUtils.convert2Endpoint(c.getPathUrl(), c.getHttpMethod(), c.getNewOperation()))
                .collect(Collectors.toList());
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
