package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedOAuthFlows;
import io.swagger.v3.oas.models.security.OAuthFlows;

import java.util.Optional;

import static com.qdesrame.openapi.diff.utils.ChangedUtils.isChanged;

/**
 * Created by adarsh.sharma on 12/01/18.
 */
public class OAuthFlowsDiff {
    private OpenApiDiff openApiDiff;

    public OAuthFlowsDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
    }

    public Optional<ChangedOAuthFlows> diff(OAuthFlows left, OAuthFlows right) {
        ChangedOAuthFlows changedOAuthFlows = new ChangedOAuthFlows(left, right);
        if (left != null && right != null) {
            openApiDiff.getoAuthFlowDiff().diff(left.getImplicit(), right.getImplicit()).ifPresent(changedOAuthFlows::setChangedImplicitOAuthFlow);
            openApiDiff.getoAuthFlowDiff().diff(left.getPassword(), right.getPassword()).ifPresent(changedOAuthFlows::setChangedPasswordOAuthFlow);
            openApiDiff.getoAuthFlowDiff().diff(left.getClientCredentials(), right.getClientCredentials()).ifPresent(changedOAuthFlows::setChangedClientCredentialOAuthFlow);
            openApiDiff.getoAuthFlowDiff().diff(left.getAuthorizationCode(), right.getAuthorizationCode()).ifPresent(changedOAuthFlows::setChangedAuthorizationCodeOAuthFlow);
        }
        return isChanged(changedOAuthFlows);
    }
}
