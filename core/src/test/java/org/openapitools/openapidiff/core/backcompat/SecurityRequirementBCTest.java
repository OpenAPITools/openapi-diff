package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.*;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SECURITY_REQUIREMENT_SCHEMES_INCREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class SecurityRequirementBCTest {
  private final String BASE = "bc_security_requirement_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_security_requirement_changed_but_compatible.yaml");
  }

  @Test
  public void schemesIncreased() {
    BackwardIncompatibleProp prop = SECURITY_REQUIREMENT_SCHEMES_INCREASED;
    assertSpecIncompatible(BASE, "bc_security_requirement_schemes_increased.yaml", prop);
  }
}
