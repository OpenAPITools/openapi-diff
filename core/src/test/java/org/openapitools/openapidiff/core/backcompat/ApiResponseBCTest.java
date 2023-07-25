package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class ApiResponseBCTest {
  private final String BASE = "bc_response_apiresponse_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_response_apiresponse_changed_but_compatible.yaml");
  }

  @Test
  public void decreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_apiresponse_decreased.yaml");
  }
}
