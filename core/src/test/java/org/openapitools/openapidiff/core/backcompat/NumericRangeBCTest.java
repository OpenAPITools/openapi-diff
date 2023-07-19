package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class NumericRangeBCTest {
  private final String BASE = "bc_numericrange_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_numericrange_changed_but_compatible.yaml");
  }

  @Test
  public void requestExclusiveMaxCreated() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_numericrange_exclusive_max_created.yaml");
  }

  @Test
  public void requestExclusiveMaxSet() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_numericrange_exclusive_max_set.yaml");
  }

  @Test
  public void requestExclusiveMinCreated() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_numericrange_exclusive_min_created.yaml");
  }

  @Test
  public void requestExclusiveMinSet() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_numericrange_exclusive_min_set.yaml");
  }

  @Test
  public void requestMaxAdded() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_numericrange_max_added.yaml");
  }

  @Test
  public void requestMaxDecreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_numericrange_max_decreased.yaml");
  }

  @Test
  public void requestMinAdded() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_numericrange_min_added.yaml");
  }

  @Test
  public void requestMinIncreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_numericrange_min_increased.yaml");
  }

  @Test
  public void responseExclusiveMaxDeleted() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_numericrange_exclusive_max_deleted.yaml");
  }

  @Test
  public void responseExclusiveMaxUnset() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_numericrange_exclusive_max_unset.yaml");
  }

  @Test
  public void responseExclusiveMinDeleted() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_numericrange_exclusive_min_deleted.yaml");
  }

  @Test
  public void responseExclusiveMinUnset() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_numericrange_exclusive_min_unset.yaml");
  }

  @Test
  public void responseMaxDeleted() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_numericrange_max_deleted.yaml");
  }

  @Test
  public void responseMaxIncreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_numericrange_max_increased.yaml");
  }

  @Test
  public void responseMinDecreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_numericrange_min_decreased.yaml");
  }

  @Test
  public void responseMinDeleted() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_numericrange_min_deleted.yaml");
  }
}
