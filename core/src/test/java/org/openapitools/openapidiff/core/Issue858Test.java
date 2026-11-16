package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

/**
 * The two specs are identical except for the {@code openapi} version field (3.0.1 vs 3.1.0) and
 * {@code info.version}. The parser represents the same {@code type} keyword differently for the two
 * versions, which used to report every operation as changed.
 */
public class Issue858Test {

  private final String OAS_30_SPEC = "issue-858-1.json";
  private final String OAS_31_SPEC = "issue-858-2.json";

  @Test
  public void testOpenApiVersionOnlyDifference30To31ReportsNoOperationChanges() {
    assertOpenApiAreEquals(OAS_30_SPEC, OAS_31_SPEC);
  }

  @Test
  public void testOpenApiVersionOnlyDifference31To30ReportsNoOperationChanges() {
    assertOpenApiAreEquals(OAS_31_SPEC, OAS_30_SPEC);
  }

  @Test
  public void testOpenApiVersionOnlyDifferenceIsUnchanged() {
    assertSpecUnchanged(OAS_30_SPEC, OAS_31_SPEC);
  }
}
