package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class HeadersBCTest {
  private final String BASE = "bc_response_headers_base.yaml";

  @Test
  public void responseHeadersUnchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void responseHeadersIncreased() {
    assertSpecChangedButCompatible(BASE, "bc_response_headers_increased.yaml");
  }

  @Test
  public void responseHeadersMissing() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_headers_missing.yaml");
  }
}
