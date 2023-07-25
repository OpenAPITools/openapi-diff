package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class ReadOnlyBCTest {
  private final String BASE = "bc_readonly_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_readonly_changed_but_compatible.yaml");
  }

  @Test
  public void requestReadOnlyIncreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_readonly_increased.yaml");
  }

  @Test
  public void requestReadOnlyRequiredDecreased() {
    // TODO: Document why desired or remove support (test added to avoid unintentional regression)
    assertOpenApiBackwardIncompatible(BASE, "bc_request_readonly_required_decreased.yaml");
  }
}
