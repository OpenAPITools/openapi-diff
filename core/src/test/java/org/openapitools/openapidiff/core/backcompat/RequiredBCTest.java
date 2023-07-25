package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class RequiredBCTest {

  private final String BASE = "bc_required_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_required_changed_but_compatible.yaml");
  }

  @Test
  public void requestRequiredIncreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_required_increased.yaml");
  }

  @Test
  public void responseRequiredDecreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_required_decreased.yaml");
  }
}
