package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.model.schema.ChangedExtensions;
import com.qdesrame.openapi.diff.utils.ChangedUtils;
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
    private ChangedExtensions changedExtensions;

    public ChangedSecurityScheme(SecurityScheme oldSecurityScheme, SecurityScheme newSecurityScheme) {
        this.oldSecurityScheme = oldSecurityScheme;
        this.newSecurityScheme = newSecurityScheme;
    }

    @Override
    public DiffResult isChanged() {
        if (!changedType && !changedDescription && !changedIn && !changedScheme && !changedBearerFormat
                && ChangedUtils.isUnchanged(changedOAuthFlows) && !changedOpenIdConnectUrl
                && (changedScopes == null || changedScopes.isUnchanged())
                && ChangedUtils.isUnchanged(changedExtensions)) {
            return DiffResult.NO_CHANGES;
        }
        if (!changedType && !changedIn && !changedScheme && !changedBearerFormat
                && ChangedUtils.isCompatible(changedOAuthFlows) && !changedOpenIdConnectUrl
                && (changedScopes == null || changedScopes.getIncreased().isEmpty())
                && ChangedUtils.isCompatible(changedExtensions)) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }
}
