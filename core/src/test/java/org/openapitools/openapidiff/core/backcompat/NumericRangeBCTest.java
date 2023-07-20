package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_NUMERIC_RANGE_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_NUMERIC_RANGE_INCREASED;

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
    assertIncompatibleRequest("bc_request_numericrange_exclusive_max_created.yaml");
  }

  @Test
  public void requestExclusiveMaxSet() {
    assertIncompatibleRequest("bc_request_numericrange_exclusive_max_set.yaml");
  }

  @Test
  public void requestExclusiveMinCreated() {
    assertIncompatibleRequest("bc_request_numericrange_exclusive_min_created.yaml");
  }

  @Test
  public void requestExclusiveMinSet() {
    assertIncompatibleRequest("bc_request_numericrange_exclusive_min_set.yaml");
  }

  @Test
  public void requestMaxAdded() {
    assertIncompatibleRequest("bc_request_numericrange_max_added.yaml");
  }

  @Test
  public void requestMaxDecreased() {
    assertIncompatibleRequest("bc_request_numericrange_max_decreased.yaml");
  }

  @Test
  public void requestMinAdded() {
    assertIncompatibleRequest("bc_request_numericrange_min_added.yaml");
  }

  @Test
  public void requestMinIncreased() {
    assertIncompatibleRequest("bc_request_numericrange_min_increased.yaml");
  }

  @Test
  public void responseExclusiveMaxDeleted() {
    assertIncompatibleResponse("bc_response_numericrange_exclusive_max_deleted.yaml");
  }

  @Test
  public void responseExclusiveMaxUnset() {
    assertIncompatibleResponse("bc_response_numericrange_exclusive_max_unset.yaml");
  }

  @Test
  public void responseExclusiveMinDeleted() {
    assertIncompatibleResponse("bc_response_numericrange_exclusive_min_deleted.yaml");
  }

  @Test
  public void responseExclusiveMinUnset() {
    assertIncompatibleResponse("bc_response_numericrange_exclusive_min_unset.yaml");
  }

  @Test
  public void responseMaxDeleted() {
    assertIncompatibleResponse("bc_response_numericrange_max_deleted.yaml");
  }

  @Test
  public void responseMaxIncreased() {
    assertIncompatibleResponse("bc_response_numericrange_max_increased.yaml");
  }

  @Test
  public void responseMinDecreased() {
    assertIncompatibleResponse("bc_response_numericrange_min_decreased.yaml");
  }

  @Test
  public void responseMinDeleted() {
    assertIncompatibleResponse("bc_response_numericrange_min_deleted.yaml");
  }

  private void assertIncompatibleRequest(String newSpec) {
    assertSpecIncompatible(BASE, newSpec, REQUEST_NUMERIC_RANGE_DECREASED);
  }

  private void assertIncompatibleResponse(String newSpec) {
    assertSpecIncompatible(BASE, newSpec, RESPONSE_NUMERIC_RANGE_INCREASED);
  }
}
