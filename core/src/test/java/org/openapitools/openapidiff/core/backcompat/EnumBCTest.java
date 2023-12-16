package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_ENUM_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_ENUM_INCREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class EnumBCTest {
  private final String BASE = "bc_enum_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_enum_changed_but_compatible.yaml");
  }

  @Test
  public void requestDecreased() {
    BackwardIncompatibleProp prop = REQUEST_ENUM_DECREASED;
    assertSpecIncompatible(BASE, "bc_request_enum_decreased.yaml", prop);
  }

  @Test
  public void responseIncreased() {
    BackwardIncompatibleProp prop = RESPONSE_ENUM_INCREASED;
    assertSpecIncompatible(BASE, "bc_response_enum_increased.yaml", prop);
  }
}
