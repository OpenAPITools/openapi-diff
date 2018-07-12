package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedOAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlow;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.qdesrame.openapi.diff.utils.ChangedUtils.isChanged;
import static java.util.Optional.ofNullable;

/**
 * Created by adarsh.sharma on 12/01/18.
 */
public class OAuthFlowDiff {
    private OpenApiDiff openApiDiff;

    public OAuthFlowDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
    }

    private static Map<String, Object> getExtensions(OAuthFlow oAuthFlow) {
        return ofNullable(oAuthFlow).map(OAuthFlow::getExtensions).orElse(null);
    }

    public Optional<ChangedOAuthFlow> diff(OAuthFlow left, OAuthFlow right) {
        ChangedOAuthFlow changedOAuthFlow = new ChangedOAuthFlow(left, right);
        if (left != null && right != null) {
            changedOAuthFlow.setChangedAuthorizationUrl(!Objects.equals(left.getAuthorizationUrl(), right.getAuthorizationUrl()));
            changedOAuthFlow.setChangedTokenUrl(!Objects.equals(left.getTokenUrl(), right.getTokenUrl()));
            changedOAuthFlow.setChangedRefreshUrl(!Objects.equals(left.getRefreshUrl(), right.getRefreshUrl()));
        }
        openApiDiff.getExtensionsDiff().diff(getExtensions(left), getExtensions(right))
                .ifPresent(changedOAuthFlow::setChangedExtensions);
        return isChanged(changedOAuthFlow);
    }

}
