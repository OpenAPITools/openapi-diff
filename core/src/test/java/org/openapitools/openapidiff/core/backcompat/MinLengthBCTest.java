package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_MIN_LENGTH_INCREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_MIN_LENGTH_INCREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class MinLengthBCTest {
  private final String BASE = "bc_minlength_base.yaml";

  @Test
  public void minLengthUnchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void requestMinLengthIncreased() {
    BackwardIncompatibleProp prop = REQUEST_MIN_LENGTH_INCREASED;
    assertSpecIncompatible(BASE, "bc_request_minlength_increased.yaml", prop);
  }

  @Test
  public void responseMinLengthIncreased() {
    BackwardIncompatibleProp prop = RESPONSE_MIN_LENGTH_INCREASED;
    assertSpecIncompatible(BASE, "bc_response_minlength_increased.yaml", prop);
  }
}
