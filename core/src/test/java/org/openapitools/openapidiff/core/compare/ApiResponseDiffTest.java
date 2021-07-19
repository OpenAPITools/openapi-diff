package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ApiResponseDiffTest extends Assertions {
  /**
   * * This is a regression test - when no responses were set, we would get an exception since the
   * OpenAPI object has a `null` ApiResponses field.
   */
  @Test
  public void testNoResponseInPrevious() {
    // The previous API had no response, so adding a response shape is still compatible.
    assertOpenApiBackwardCompatible(
        "backwardCompatibility/apiResponse_diff_1.yaml",
        "backwardCompatibility/apiResponse_diff_2.yaml",
        true);

    // Removing the response shape is backwards incompatible.
    assertOpenApiBackwardIncompatible(
        "backwardCompatibility/apiResponse_diff_2.yaml",
        "backwardCompatibility/apiResponse_diff_1.yaml");
  }
}
