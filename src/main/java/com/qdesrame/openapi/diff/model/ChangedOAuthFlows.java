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
    private final OAuthFlows oldOAuthFlows;
    private final OAuthFlows newOAuthFlows;

    private ChangedOAuthFlow changedImplicitOAuthFlow;
    private ChangedOAuthFlow changedPasswordOAuthFlow;
    private ChangedOAuthFlow changedClientCredentialOAuthFlow;
    private ChangedOAuthFlow changedAuthorizationCodeOAuthFlow;

    public ChangedOAuthFlows(OAuthFlows oldOAuthFlows, OAuthFlows newOAuthFlows) {
        this.oldOAuthFlows = oldOAuthFlows;
        this.newOAuthFlows = newOAuthFlows;
    }

    @Override
    public DiffResult isChanged() {
        if ((changedImplicitOAuthFlow == null || changedImplicitOAuthFlow.isUnchanged())
                && (changedPasswordOAuthFlow == null || changedPasswordOAuthFlow.isUnchanged())
                && (changedClientCredentialOAuthFlow == null || changedClientCredentialOAuthFlow.isUnchanged())
                && (changedAuthorizationCodeOAuthFlow == null || changedAuthorizationCodeOAuthFlow.isUnchanged())) {
            return DiffResult.NO_CHANGES;
        }
        if ((changedImplicitOAuthFlow == null || changedImplicitOAuthFlow.isCompatible())
                && (changedPasswordOAuthFlow == null || changedPasswordOAuthFlow.isCompatible())
                && (changedClientCredentialOAuthFlow == null || changedClientCredentialOAuthFlow.isCompatible())
                && (changedAuthorizationCodeOAuthFlow == null || changedAuthorizationCodeOAuthFlow.isCompatible())) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }

}
