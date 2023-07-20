package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_HEADER_EXPLODE_CHANGED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_HEADER_REQUIRED_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_HEADER_REQUIRED_INCREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class HeaderBCTest {
  private final String BASE = "bc_response_header_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_response_header_changed_but_compatible.yaml");
  }

  @Test
  public void responseExplodeChanged() {
    BackwardIncompatibleProp prop = RESPONSE_HEADER_EXPLODE_CHANGED;
    assertSpecIncompatible(BASE, "bc_response_header_explode_changed.yaml", prop);
  }

  @Test
  public void responseRequiredDecreased() {
    BackwardIncompatibleProp prop = RESPONSE_HEADER_REQUIRED_DECREASED;
    assertSpecIncompatible(BASE, "bc_response_header_required_decreased.yaml", prop);
  }

  @Test
  public void responseRequiredIncreased() {
    // TODO: Document why desired or remove support (test added to avoid unintentional regression)
    BackwardIncompatibleProp prop = RESPONSE_HEADER_REQUIRED_INCREASED;
    assertSpecIncompatible(BASE, "bc_response_header_required_increased.yaml", prop);
  }
}
