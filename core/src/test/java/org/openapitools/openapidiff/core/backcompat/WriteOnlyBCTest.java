package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class WriteOnlyBCTest {
  private final String BASE = "bc_writeonly_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_writeonly_changed_but_compatible.yaml");
  }

  @Test
  public void responseWriteOnlyIncreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_writeonly_increased.yaml");
  }

  @Test
  public void responseWriteOnlyRequiredDecreased() {
    // TODO: Document why desired or remove support (test added to avoid unintentional regression)
    assertOpenApiBackwardIncompatible(BASE, "bc_response_writeonly_required_decreased.yaml");
  }
}
