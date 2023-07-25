package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class EnumBCTest {
  private final String BASE = "bc_enum_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_enum_changed_but_compatible.yaml");
  }

  @Test
  public void requestDecreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_enum_decreased.yaml");
  }

  @Test
  public void responseIncreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_enum_increased.yaml");
  }
}
