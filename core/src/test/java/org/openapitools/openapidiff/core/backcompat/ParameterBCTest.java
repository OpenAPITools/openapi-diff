package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class ParameterBCTest {
  private final String BASE = "bc_request_param_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_request_param_changed_but_compatible.yaml");
  }

  @Test
  public void allowEmptyValueDecreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_param_allowemptyvalue_decreased.yaml");
  }

  @Test
  public void explodeChanged() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_param_explode_changed.yaml");
  }

  @Test
  public void requiredIncreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_param_required_increased.yaml");
  }

  @Test
  public void styleChanged() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_param_style_changed.yaml");
  }
}
