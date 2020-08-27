package com.qdesrame.openapi.diff.core.model;

import io.swagger.v3.oas.models.security.OAuthFlow;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangedOAuthFlow implements ComposedChanged {

  private OAuthFlow oldOAuthFlow;
  private OAuthFlow newOAuthFlow;

  private boolean authorizationUrl;
  private boolean tokenUrl;
  private boolean refreshUrl;
  private ChangedExtensions extensions;

  public ChangedOAuthFlow(OAuthFlow oldOAuthFlow, OAuthFlow newOAuthFlow) {
    this.oldOAuthFlow = oldOAuthFlow;
    this.newOAuthFlow = newOAuthFlow;
  }

  @Override
  public List<Changed> getChangedElements() {
    return Collections.singletonList(extensions);
  }

  @Override
  public DiffResult isCoreChanged() {
    if (authorizationUrl || tokenUrl || refreshUrl) {
      return DiffResult.INCOMPATIBLE;
    }
    return DiffResult.NO_CHANGES;
  }
}
