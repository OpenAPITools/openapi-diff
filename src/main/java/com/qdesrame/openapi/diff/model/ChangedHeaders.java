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
    private Map<String, Header> oldHeaders;
    private Map<String, Header> newHeaders;

    private Map<String, Header> increased;
    private Map<String, Header> missing;
    private Map<String, ChangedHeader> changed;

    public ChangedHeaders(Map<String, Header> oldHeaders, Map<String, Header> newHeaders) {
        this.oldHeaders = oldHeaders;
        this.newHeaders = newHeaders;
    }

    @Override
    public boolean isDiff() {
        return !increased.isEmpty()
                || !missing.isEmpty()
                || (changed != null && !changed.isEmpty());
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return missing.isEmpty()
                && (changed == null || changed.values().stream().allMatch(c -> c.isDiffBackwardCompatible()));
    }
}
