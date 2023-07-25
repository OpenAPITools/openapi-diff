package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class OpenApiBCTest {
  private final String BASE = "bc_openapi_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_openapi_changed_but_compatible.yaml");
  }

  @Test
  public void endpointsDecreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_openapi_endpoints_decreased.yaml");
  }
}
