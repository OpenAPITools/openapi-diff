package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class OperationBCTest {
  private final String BASE = "bc_operation_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_operation_changed_but_compatible.yaml");
  }

  @Test
  public void operationIdChangedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_operation_changed_incompatible_operation_id.yaml");
  }

  @Test
  public void operationIdChangedInCompatibleWithFlagSet() {
    assertSpecIncompatible(BASE, "bc_operation_changed_incompatible_operation_id.yaml", BackwardIncompatibleProp.OPERATION_ID_CHANGED);
  }
}
