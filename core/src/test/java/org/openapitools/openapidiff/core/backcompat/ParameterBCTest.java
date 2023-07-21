package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_PARAMS_REQUIRED_INCREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_PARAM_ALLOWEMPTY_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_PARAM_EXPLODE_CHANGED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_PARAM_STYLE_CHANGED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

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
    BackwardIncompatibleProp prop = REQUEST_PARAM_ALLOWEMPTY_DECREASED;
    assertSpecIncompatible(BASE, "bc_request_param_allowemptyvalue_decreased.yaml", prop);
  }

  @Test
  public void explodeChanged() {
    BackwardIncompatibleProp prop = REQUEST_PARAM_EXPLODE_CHANGED;
    assertSpecIncompatible(BASE, "bc_request_param_explode_changed.yaml", prop);
  }

  @Test
  public void requiredIncreased() {
    BackwardIncompatibleProp prop = REQUEST_PARAMS_REQUIRED_INCREASED;
    assertSpecIncompatible(BASE, "bc_request_param_required_increased.yaml", prop);
  }

  @Test
  public void styleChanged() {
    BackwardIncompatibleProp prop = REQUEST_PARAM_STYLE_CHANGED;
    assertSpecIncompatible(BASE, "bc_request_param_style_changed.yaml", prop);
  }
}
