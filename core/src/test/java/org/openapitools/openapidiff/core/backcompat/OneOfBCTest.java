package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class OneOfBCTest {
  private final String BASE = "bc_oneof_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_oneof_changed_but_compatible.yaml");
  }

  @Test
  public void requestOneOfDecreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_oneof_decreased.yaml");
  }

  @Test
  public void responseOneOfIncreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_oneof_increased.yaml");
  }
}
