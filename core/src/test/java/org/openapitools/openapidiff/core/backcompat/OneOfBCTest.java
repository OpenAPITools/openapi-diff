package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_ONEOF_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_ONEOF_INCREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

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
  public void requestDecreased() {
    BackwardIncompatibleProp prop = REQUEST_ONEOF_DECREASED;
    assertSpecIncompatible(BASE, "bc_request_oneof_decreased.yaml", prop);
  }

  @Test
  public void responseIncreased() {
    BackwardIncompatibleProp prop = RESPONSE_ONEOF_INCREASED;
    assertSpecIncompatible(BASE, "bc_response_oneof_increased.yaml", prop);
  }
}
