package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class MaxLengthBCTest {
  private final String BASE = "bc_maxlength_base.yaml";

  @Test
  public void maxLengthUnchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void requestMaxLengthDecreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_maxlength_decreased.yaml");
  }

  @Test
  public void responseMaxLengthIncreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_maxlength_increased.yaml");
  }
}
