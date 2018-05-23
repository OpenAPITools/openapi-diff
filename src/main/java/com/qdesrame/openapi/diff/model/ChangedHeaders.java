package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.headers.Header;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
@Getter
@Setter
public class ChangedHeaders implements Changed {
    private final Map<String, Header> oldHeaders;
    private final Map<String, Header> newHeaders;
    private final DiffContext context;

    private Map<String, Header> increased;
    private Map<String, Header> missing;
    private Map<String, ChangedHeader> changed;

    public ChangedHeaders(Map<String, Header> oldHeaders, Map<String, Header> newHeaders, DiffContext context) {
        this.oldHeaders = oldHeaders;
        this.newHeaders = newHeaders;
        this.context = context;
    }

    @Override
    public DiffResult isChanged() {
        if (increased.isEmpty() && missing.isEmpty() && (changed == null || changed.isEmpty())) {
            return DiffResult.NO_CHANGES;
        }
        if (missing.isEmpty() && (changed == null || changed.values().stream().allMatch(Changed::isCompatible))) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }
}
