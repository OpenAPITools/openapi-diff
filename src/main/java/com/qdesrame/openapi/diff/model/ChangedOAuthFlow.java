package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.security.OAuthFlow;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by adarsh.sharma on 12/01/18.
 */
@Getter
@Setter
public class ChangedOAuthFlow implements Changed {
    private OAuthFlow oldOAuthFlow;
    private OAuthFlow newOAuthFlow;

    private boolean changedAuthorizationUrl;
    private boolean changedTokenUrl;
    private boolean changedRefreshUrl;

    public ChangedOAuthFlow(OAuthFlow oldOAuthFlow, OAuthFlow newOAuthFlow) {
        this.oldOAuthFlow = oldOAuthFlow;
        this.newOAuthFlow = newOAuthFlow;
    }

    @Override
    public DiffResult isChanged() {
        if (!changedAuthorizationUrl && !changedTokenUrl && !changedRefreshUrl) {
            return DiffResult.NO_CHANGES;
        } else {
            return DiffResult.INCOMPATIBLE;
        }
    }
}
