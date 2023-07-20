package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_WRITEONLY_INCREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_WRITEONLY_REQUIRED_DECREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class WriteOnlyBCTest {
  private final String BASE = "bc_writeonly_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_writeonly_changed_but_compatible.yaml");
  }

  @Test
  public void responseWriteOnlyIncreased() {
    BackwardIncompatibleProp prop = RESPONSE_WRITEONLY_INCREASED;
    assertSpecIncompatible(BASE, "bc_response_writeonly_increased.yaml", prop);
  }

  @Test
  public void responseWriteOnlyRequiredDecreased() {
    BackwardIncompatibleProp prop = RESPONSE_WRITEONLY_REQUIRED_DECREASED;
    // TODO: Document why desired or remove support (test added to avoid unintentional regression)
    assertSpecIncompatible(BASE, "bc_response_writeonly_required_decreased.yaml", prop);
  }
}
