package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_READONLY_INCREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_READONLY_REQUIRED_DECREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class ReadOnlyBCTest {
  private final String BASE = "bc_readonly_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_readonly_changed_but_compatible.yaml");
  }

  @Test
  public void requestReadOnlyIncreased() {
    BackwardIncompatibleProp prop = REQUEST_READONLY_INCREASED;
    assertSpecIncompatible(BASE, "bc_request_readonly_increased.yaml", prop);
  }

  @Test
  public void requestReadOnlyRequiredDecreased() {
    // Incompatible because a prev RO prop (invalid) is now not RO and required
    BackwardIncompatibleProp prop = REQUEST_READONLY_REQUIRED_DECREASED;
    assertSpecIncompatible(BASE, "bc_request_readonly_required_decreased.yaml", prop);
  }
}
