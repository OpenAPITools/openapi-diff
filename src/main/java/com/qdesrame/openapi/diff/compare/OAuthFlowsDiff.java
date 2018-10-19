package com.qdesrame.openapi.diff.compare;

import static com.qdesrame.openapi.diff.utils.ChangedUtils.isChanged;
import static java.util.Optional.ofNullable;

import com.qdesrame.openapi.diff.model.ChangedOAuthFlows;
import io.swagger.v3.oas.models.security.OAuthFlows;
import java.util.Map;
import java.util.Optional;

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
          .ifPresent(changedOAuthFlows::setChangedImplicitOAuthFlow);
      openApiDiff
          .getOAuthFlowDiff()
          .diff(left.getPassword(), right.getPassword())
          .ifPresent(changedOAuthFlows::setChangedPasswordOAuthFlow);
      openApiDiff
          .getOAuthFlowDiff()
          .diff(left.getClientCredentials(), right.getClientCredentials())
          .ifPresent(changedOAuthFlows::setChangedClientCredentialOAuthFlow);
      openApiDiff
          .getOAuthFlowDiff()
          .diff(left.getAuthorizationCode(), right.getAuthorizationCode())
          .ifPresent(changedOAuthFlows::setChangedAuthorizationCodeOAuthFlow);
    }
    openApiDiff
        .getExtensionsDiff()
        .diff(getExtensions(left), getExtensions(right))
        .ifPresent(changedOAuthFlows::setChangedExtensions);
    return isChanged(changedOAuthFlows);
  }
}
