package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.*;

import org.junit.jupiter.api.Test;

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
    assertOpenApiBackwardIncompatible(BASE, "bc_security_requirement_schemes_increased.yaml");
  }
}
