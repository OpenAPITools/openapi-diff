package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardCompatible;

import org.junit.jupiter.api.Test;

/** Created by trohrberg on 23/03/19. */
public class AddPropPutDiffTest {
  private final String OPENAPI_DOC1 = "add-prop-put-1.yaml";
  private final String OPENAPI_DOC2 = "add-prop-put-2.yaml";

  @Test
  public void testDiffSame() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
  }

  @Test
  public void testDiffReportsNonBreakingChange() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC1, OPENAPI_DOC2, true);
  }
}
