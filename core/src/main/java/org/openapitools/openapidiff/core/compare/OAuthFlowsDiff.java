package org.openapitools.openapidiff.core.compare;

import static java.util.Optional.ofNullable;
import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;

import io.swagger.v3.oas.models.security.OAuthFlows;
import java.util.Map;
import java.util.Optional;
import org.openapitools.openapidiff.core.model.ChangedOAuthFlows;

/** Created by adarsh.sharma on 12/01/18. */
public class OAuthFlowsDiff {
  private OpenApiDiff openApiDiff;

  public OAuthFlowsDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  private static Map<String, Object> getExtensions(OAuthFlows oAuthFlow) {
    return ofNullable(oAuthFlow).map(OAuthFlows::getExtensions).orElse(null);
  }

  public Optional<ChangedOAuthFlows> diff(OAuthFlows left, OAuthFlows right) {
    ChangedOAuthFlows changedOAuthFlows = new ChangedOAuthFlows(left, right);
    if (left != null && right != null) {
      openApiDiff
          .getOAuthFlowDiff()
          .diff(left.getImplicit(), right.getImplicit())
          .ifPresent(changedOAuthFlows::setImplicitOAuthFlow);
      openApiDiff
          .getOAuthFlowDiff()
          .diff(left.getPassword(), right.getPassword())
          .ifPresent(changedOAuthFlows::setPasswordOAuthFlow);
      openApiDiff
          .getOAuthFlowDiff()
          .diff(left.getClientCredentials(), right.getClientCredentials())
          .ifPresent(changedOAuthFlows::setClientCredentialOAuthFlow);
      openApiDiff
          .getOAuthFlowDiff()
          .diff(left.getAuthorizationCode(), right.getAuthorizationCode())
          .ifPresent(changedOAuthFlows::setAuthorizationCodeOAuthFlow);
    }
    openApiDiff
        .getExtensionsDiff()
        .diff(getExtensions(left), getExtensions(right))
        .ifPresent(changedOAuthFlows::setExtensions);
    return isChanged(changedOAuthFlows);
  }
}
