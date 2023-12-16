package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.*;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.*;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class SecuritySchemeBCTest {
  private final String BASE = "bc_security_scheme_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_security_scheme_changed_but_compatible.yaml");
  }

  @Test
  public void bearerFormatChanged() {
    BackwardIncompatibleProp prop = SECURITY_SCHEME_BEARER_FORMAT_CHANGED;
    assertSpecIncompatible(BASE, "bc_security_scheme_bearer_format_changed.yaml", prop);
  }

  @Test
  public void inChanged() {
    assertOpenApiBackwardIncompatible(BASE, "bc_security_scheme_in_changed.yaml");
  }

  @Test
  public void openIdConnectUrlChanged() {
    BackwardIncompatibleProp prop = SECURITY_SCHEME_OPENIDCONNECT_URL_CHANGED;
    assertSpecIncompatible(BASE, "bc_security_scheme_open_id_connect_url_changed.yaml", prop);
  }

  @Test
  public void schemeChanged() {
    BackwardIncompatibleProp prop = SECURITY_SCHEME_SCHEME_CHANGED;
    assertSpecIncompatible(BASE, "bc_security_scheme_scheme_changed.yaml", prop);
  }

  @Test
  public void typeChanged() {
    assertOpenApiBackwardIncompatible(BASE, "bc_security_scheme_type_changed.yaml");
  }

  @Test
  public void scopesIncreased() {
    BackwardIncompatibleProp prop = SECURITY_SCHEME_SCOPES_INCREASED;
    assertSpecIncompatible(BASE, "bc_security_scheme_scopes_increased.yaml", prop);
  }
}
