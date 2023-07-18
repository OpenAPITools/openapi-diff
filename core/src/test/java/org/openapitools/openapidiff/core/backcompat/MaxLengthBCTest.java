package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_MAX_LENGTH_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_MAX_LENGTH_INCREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class MaxLengthBCTest {
  private final String BASE = "bc_maxlength_base.yaml";

  @Test
  public void maxLengthUnchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void requestMaxLengthDecreased() {
    BackwardIncompatibleProp prop = REQUEST_MAX_LENGTH_DECREASED;
    assertSpecIncompatible(BASE, "bc_request_maxlength_decreased.yaml", prop);
  }

  @Test
  public void responseMaxLengthIncreased() {
    BackwardIncompatibleProp prop = RESPONSE_MAX_LENGTH_INCREASED;
    assertSpecIncompatible(BASE, "bc_response_maxlength_increased.yaml", prop);
  }
}
