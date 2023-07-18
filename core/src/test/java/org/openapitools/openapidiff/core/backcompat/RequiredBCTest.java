package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_REQUIRED_INCREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_REQUIRED_DECREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

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
    BackwardIncompatibleProp prop = REQUEST_REQUIRED_INCREASED;
    assertSpecIncompatible(BASE, "bc_request_required_increased.yaml", prop);
  }

  @Test
  public void responseRequiredDecreased() {
    BackwardIncompatibleProp prop = RESPONSE_REQUIRED_DECREASED;
    assertSpecIncompatible(BASE, "bc_response_required_decreased.yaml", prop);
  }
}
