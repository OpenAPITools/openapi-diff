package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.OPENAPI_ENDPOINTS_DECREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

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
    BackwardIncompatibleProp prop = OPENAPI_ENDPOINTS_DECREASED;
    assertSpecIncompatible(BASE, "bc_openapi_endpoints_decreased.yaml", prop);
  }
}
