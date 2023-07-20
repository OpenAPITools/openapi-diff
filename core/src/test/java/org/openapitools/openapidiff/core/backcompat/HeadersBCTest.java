package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_HEADERS_DECREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class HeadersBCTest {
  private final String BASE = "bc_response_headers_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_response_headers_changed_but_compatible.yaml");
  }

  @Test
  public void responseDecreased() {
    BackwardIncompatibleProp prop = RESPONSE_HEADERS_DECREASED;
    assertSpecIncompatible(BASE, "bc_response_headers_decreased.yaml", prop);
  }
}
