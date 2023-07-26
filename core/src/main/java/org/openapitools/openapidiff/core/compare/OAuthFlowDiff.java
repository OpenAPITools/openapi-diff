package org.openapitools.openapidiff.core.compare;

import static java.util.Optional.ofNullable;
import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;

import io.swagger.v3.oas.models.security.OAuthFlow;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.openapitools.openapidiff.core.model.ChangedOAuthFlow;
import org.openapitools.openapidiff.core.model.DiffContext;

public class OAuthFlowDiff {
  private final OpenApiDiff openApiDiff;

  public OAuthFlowDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  private static Map<String, Object> getExtensions(OAuthFlow oAuthFlow) {
    return ofNullable(oAuthFlow).map(OAuthFlow::getExtensions).orElse(null);
  }

  public Optional<ChangedOAuthFlow> diff(OAuthFlow left, OAuthFlow right, DiffContext context) {
    ChangedOAuthFlow changedOAuthFlow = new ChangedOAuthFlow(left, right, context);
    if (left != null && right != null) {
      changedOAuthFlow
          .setAuthorizationUrl(
              !Objects.equals(left.getAuthorizationUrl(), right.getAuthorizationUrl()))
          .setTokenUrl(!Objects.equals(left.getTokenUrl(), right.getTokenUrl()))
          .setRefreshUrl(!Objects.equals(left.getRefreshUrl(), right.getRefreshUrl()));
    }
    openApiDiff
        .getExtensionsDiff()
        .diff(getExtensions(left), getExtensions(right))
        .ifPresent(changedOAuthFlow::setExtensions);
    return isChanged(changedOAuthFlow);
  }
}
