package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by adarsh.sharma on 11/01/18.
 */
@Getter
@Setter
public class ChangedSecurityScheme implements Changed {
    private SecurityScheme oldSecurityScheme;
    private SecurityScheme newSecurityScheme;
    private boolean changedType;
    private boolean changedDescription;
    private boolean changedIn;
    private boolean changedScheme;
    private boolean changedBearerFormat;
    private ChangedOAuthFlows changedOAuthFlows;
    private boolean changedOpenIdConnectUrl;
    private ListDiff<String> changedScopes;

    public ChangedSecurityScheme(SecurityScheme oldSecurityScheme, SecurityScheme newSecurityScheme) {
        this.oldSecurityScheme = oldSecurityScheme;
        this.newSecurityScheme = newSecurityScheme;
    }

    @Override
    public boolean isDiff() {
        return changedType ||
                changedDescription ||
                changedIn ||
                changedScheme ||
                changedBearerFormat ||
                (changedOAuthFlows != null && changedOAuthFlows.isDiff()) ||
                changedOpenIdConnectUrl ||
                changedScopes != null;
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return !changedType &&
                !changedIn &&
                !changedScheme &&
                !changedBearerFormat &&
                (changedOAuthFlows == null || changedOAuthFlows.isDiffBackwardCompatible()) &&
                !changedOpenIdConnectUrl &&
                (changedScopes == null || changedScopes.getIncreased().isEmpty());
    }
}
