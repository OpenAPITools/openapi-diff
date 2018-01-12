package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.security.OAuthFlows;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by adarsh.sharma on 12/01/18.
 */
@Getter
@Setter
public class ChangedOAuthFlows implements Changed {
    private OAuthFlows oldOAuthFlows;
    private OAuthFlows newOAuthFlows;

    private ChangedOAuthFlow changedImplicitOAuthFlow;
    private ChangedOAuthFlow changedPasswordOAuthFlow;
    private ChangedOAuthFlow changedClientCredentialOAuthFlow;
    private ChangedOAuthFlow changedAuthorizationCodeOAuthFlow;

    public ChangedOAuthFlows(OAuthFlows oldOAuthFlows, OAuthFlows newOAuthFlows) {
        this.oldOAuthFlows = oldOAuthFlows;
        this.newOAuthFlows = newOAuthFlows;
    }

    @Override
    public boolean isDiff() {
        return (changedImplicitOAuthFlow != null && changedImplicitOAuthFlow.isDiff()) ||
                (changedPasswordOAuthFlow != null && changedPasswordOAuthFlow.isDiff()) ||
                (changedClientCredentialOAuthFlow != null && changedClientCredentialOAuthFlow.isDiff()) ||
                (changedAuthorizationCodeOAuthFlow != null && changedAuthorizationCodeOAuthFlow.isDiff());
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return (changedImplicitOAuthFlow == null || changedImplicitOAuthFlow.isDiffBackwardCompatible()) &&
                (changedPasswordOAuthFlow == null || changedPasswordOAuthFlow.isDiffBackwardCompatible()) &&
                (changedClientCredentialOAuthFlow == null || changedClientCredentialOAuthFlow.isDiffBackwardCompatible()) &&
                (changedAuthorizationCodeOAuthFlow == null || changedAuthorizationCodeOAuthFlow.isDiffBackwardCompatible());
    }
}
