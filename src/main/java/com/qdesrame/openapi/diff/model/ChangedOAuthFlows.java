package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.model.schema.ChangedExtensions;
import com.qdesrame.openapi.diff.utils.ChangedUtils;
import io.swagger.v3.oas.models.security.OAuthFlows;
import lombok.Getter;
import lombok.Setter;

/** Created by adarsh.sharma on 12/01/18. */
@Getter
@Setter
public class ChangedOAuthFlows implements Changed {
  private final OAuthFlows oldOAuthFlows;
  private final OAuthFlows newOAuthFlows;

  private ChangedOAuthFlow changedImplicitOAuthFlow;
  private ChangedOAuthFlow changedPasswordOAuthFlow;
  private ChangedOAuthFlow changedClientCredentialOAuthFlow;
  private ChangedOAuthFlow changedAuthorizationCodeOAuthFlow;
  private ChangedExtensions changedExtensions;

  public ChangedOAuthFlows(OAuthFlows oldOAuthFlows, OAuthFlows newOAuthFlows) {
    this.oldOAuthFlows = oldOAuthFlows;
    this.newOAuthFlows = newOAuthFlows;
  }

  @Override
  public DiffResult isChanged() {
    if ((changedImplicitOAuthFlow == null || changedImplicitOAuthFlow.isUnchanged())
        && ChangedUtils.isUnchanged(changedPasswordOAuthFlow)
        && ChangedUtils.isUnchanged(changedClientCredentialOAuthFlow)
        && ChangedUtils.isUnchanged(changedAuthorizationCodeOAuthFlow)
        && ChangedUtils.isUnchanged(changedExtensions)) {
      return DiffResult.NO_CHANGES;
    }
    if ((changedImplicitOAuthFlow == null || changedImplicitOAuthFlow.isCompatible())
        && ChangedUtils.isCompatible(changedPasswordOAuthFlow)
        && ChangedUtils.isCompatible(changedClientCredentialOAuthFlow)
        && ChangedUtils.isCompatible(changedAuthorizationCodeOAuthFlow)
        && ChangedUtils.isCompatible(changedExtensions)) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
