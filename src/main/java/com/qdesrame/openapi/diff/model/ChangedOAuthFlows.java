package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.model.schema.ChangedExtensions;
import io.swagger.v3.oas.models.security.OAuthFlows;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/** Created by adarsh.sharma on 12/01/18. */
@Getter
@Setter
@Accessors(chain = true)
public class ChangedOAuthFlows implements ComposedChanged {
  private final OAuthFlows oldOAuthFlows;
  private final OAuthFlows newOAuthFlows;

  private ChangedOAuthFlow implicitOAuthFlow;
  private ChangedOAuthFlow passwordOAuthFlow;
  private ChangedOAuthFlow clientCredentialOAuthFlow;
  private ChangedOAuthFlow authorizationCodeOAuthFlow;
  private ChangedExtensions extensions;

  public ChangedOAuthFlows(OAuthFlows oldOAuthFlows, OAuthFlows newOAuthFlows) {
    this.oldOAuthFlows = oldOAuthFlows;
    this.newOAuthFlows = newOAuthFlows;
  }

  @Override
  public List<Changed> getChangedElements() {
    return Arrays.asList(
        implicitOAuthFlow,
        passwordOAuthFlow,
        clientCredentialOAuthFlow,
        authorizationCodeOAuthFlow,
        extensions);
  }

  @Override
  public DiffResult isCoreChanged() {
    return DiffResult.NO_CHANGES;
  }
}
