package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class RequestBodyBCTest {
  private final String BASE = "bc_request_body_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void requiredChanged() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_body_required_changed.yaml");
  }
}
