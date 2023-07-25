package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class SecurityRequirementsBCTest {
  private final String BASE = "bc_security_requirements_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_security_requirements_changed_but_compatible.yaml");
  }

  // TODO: Dropping *all* security requirements should be compatible. Refactor or document
  // reasoning. Context: OAS spec is clear that only one of the security requirement objects
  // need to be satisfied so it makes sense why dropping one could break a client that may
  // not yet support one of the remaining referenced security schemes. But dropping all
  // requirements should be compatible.
  @Test
  public void decreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_security_requirements_decreased.yaml");
  }

  // TODO: A missing incompatible check seems to be if requirements increase from zero to 1 or more

  @Test
  public void schemeTypeChanged() {
    assertOpenApiBackwardIncompatible(BASE, "bc_security_requirements_scheme_type_changed.yaml");
  }
}
