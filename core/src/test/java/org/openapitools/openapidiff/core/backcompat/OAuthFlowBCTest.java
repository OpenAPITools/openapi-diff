package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SECURITY_SCHEME_OAUTH2_AUTH_URL_CHANGED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SECURITY_SCHEME_OAUTH2_REFRESH_URL_CHANGED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SECURITY_SCHEME_OAUTH2_TOKEN_URL_CHANGED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class OAuthFlowBCTest {
  private final String BASE = "bc_oauthflow_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void authorizationUrlChanged() {
    BackwardIncompatibleProp prop = SECURITY_SCHEME_OAUTH2_AUTH_URL_CHANGED;
    assertSpecIncompatible(BASE, "bc_oauthflow_authorization_url_changed.yaml", prop);
  }

  @Test
  public void refreshUrlChanged() {
    BackwardIncompatibleProp prop = SECURITY_SCHEME_OAUTH2_REFRESH_URL_CHANGED;
    assertSpecIncompatible(BASE, "bc_oauthflow_refresh_url_changed.yaml", prop);
  }

  @Test
  public void tokenUrlChanged() {
    BackwardIncompatibleProp prop = SECURITY_SCHEME_OAUTH2_TOKEN_URL_CHANGED;
    assertSpecIncompatible(BASE, "bc_oauthflow_token_url_changed.yaml", prop);
  }
}
