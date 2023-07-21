package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_PARAMS_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_PARAMS_REQUIRED_INCREASED;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class ParametersBCTest {
  private final String BASE = "bc_request_params_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_request_params_changed_but_compatible.yaml");
  }

  @Test
  public void decreased() {
    BackwardIncompatibleProp prop = REQUEST_PARAMS_DECREASED;
    assertSpecIncompatible(BASE, "bc_request_params_decreased.yaml", prop);
  }

  @Test
  public void requiredIncreased() {
    BackwardIncompatibleProp prop = REQUEST_PARAMS_REQUIRED_INCREASED;
    assertSpecIncompatible(BASE, "bc_request_params_required_increased.yaml", prop);
  }
}
