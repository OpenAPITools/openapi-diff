package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class OAuthFlowBCTest {
  private final String BASE = "bc_oauthflow_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void authorizationUrlChanged() {
    assertOpenApiBackwardIncompatible(BASE, "bc_oauthflow_authorization_url_changed.yaml");
  }

  @Test
  public void refreshUrlChanged() {
    assertOpenApiBackwardIncompatible(BASE, "bc_oauthflow_refresh_url_changed.yaml");
  }

  @Test
  public void tokenUrlChanged() {
    assertOpenApiBackwardIncompatible(BASE, "bc_oauthflow_token_url_changed.yaml");
  }
}
