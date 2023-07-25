package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class HeaderBCTest {
  private final String BASE = "bc_response_header_base.yaml";

  @Test
  public void responseHeaderUnchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void responseHeaderDeprecated() {
    assertSpecChangedButCompatible(BASE, "bc_response_header_deprecated.yaml");
  }

  @Test
  public void responseHeaderRequiredAdded() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_header_required_added.yaml");
  }

  @Test
  public void responseHeaderRequiredDeleted() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_header_required_deleted.yaml");
  }

  @Test
  public void responseHeaderExplode() {
    String RESPONSE_HEADER_EXPLODE = "bc_response_header_explode.yaml";
    assertOpenApiBackwardIncompatible(BASE, RESPONSE_HEADER_EXPLODE);
  }
}
