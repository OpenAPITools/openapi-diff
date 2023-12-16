package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_CONTENT_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_CONTENT_DECREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class ContentBCTest {
  private final String BASE = "bc_content_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_content_changed_but_compatible.yaml");
  }

  @Test
  public void requestDecreased() {
    BackwardIncompatibleProp prop = REQUEST_CONTENT_DECREASED;
    assertSpecIncompatible(BASE, "bc_request_content_decreased.yaml", prop);
  }

  @Test
  public void responseDecreased() {
    BackwardIncompatibleProp prop = RESPONSE_CONTENT_DECREASED;
    assertSpecIncompatible(BASE, "bc_response_content_decreased.yaml", prop);
  }
}
