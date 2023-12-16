package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.OPENAPI_ENDPOINTS_DECREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class RequestBodyBCTest {
  private final String BASE = "bc_request_body_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void requiredChanged() {
    BackwardIncompatibleProp prop = OPENAPI_ENDPOINTS_DECREASED;
    assertSpecIncompatible(BASE, "bc_request_body_required_changed.yaml", prop);
  }
}
