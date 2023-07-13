package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

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
    assertOpenApiBackwardIncompatible(BASE, "bc_security_scheme_bearer_format_changed.yaml");
  }

  @Test
  public void inChanged() {
    assertOpenApiBackwardIncompatible(BASE, "bc_security_scheme_in_changed.yaml");
  }

  @Test
  public void openIdConnectUrlChanged() {
    assertOpenApiBackwardIncompatible(BASE, "bc_security_scheme_open_id_connect_url_changed.yaml");
  }

  @Test
  public void schemeChanged() {
    assertOpenApiBackwardIncompatible(BASE, "bc_security_scheme_scheme_changed.yaml");
  }

  @Test
  public void typeChanged() {
    assertOpenApiBackwardIncompatible(BASE, "bc_security_scheme_type_changed.yaml");
  }

  @Test
  public void scopesIncreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_security_scheme_scopes_increased.yaml");
  }
}
