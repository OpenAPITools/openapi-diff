package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class ParametersBCTest {
  private final String BASE = "bc_request_params_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_request_params_changed_but_compatible.yaml");
  }

  @Test
  public void decreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_params_decreased.yaml");
  }

  @Test
  public void requiredIncreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_params_required_increased.yaml");
  }
}
