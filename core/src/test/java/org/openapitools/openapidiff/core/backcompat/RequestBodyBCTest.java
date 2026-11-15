package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_BODY_REQUIRED_INCREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class RequestBodyBCTest {
  private final String BASE = "bc_request_body_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void requiredIncreased() {
    // optional -> required: clients that do not send the body break
    BackwardIncompatibleProp prop = REQUEST_BODY_REQUIRED_INCREASED;
    assertSpecIncompatible(BASE, "bc_request_body_required_changed.yaml", prop);
  }

  @Test
  public void requiredDecreasedButCompatible() {
    // required -> optional: existing clients that keep sending the body still
    // work (issue #329)
    assertSpecChangedButCompatible("bc_request_body_required_changed.yaml", BASE);
  }
}
