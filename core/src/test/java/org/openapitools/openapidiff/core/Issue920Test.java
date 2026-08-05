package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;

import org.junit.jupiter.api.Test;

public class Issue920Test {

  private final String DIRECT_ARRAY_STRING_ITEM = "issue-920-1.yaml";
  private final String ALLOF_ARRAY_INTEGER_ITEM = "issue-920-2.yaml";

  /**
   * Items must still be compared when a direct array is replaced by an allOf-wrapped array,
   * otherwise falling through to a plain schema diff would silently hide item changes.
   */
  @Test
  public void testDirectArrayToAllOfArrayDetectsChangedItemType() {
    assertOpenApiBackwardIncompatible(DIRECT_ARRAY_STRING_ITEM, ALLOF_ARRAY_INTEGER_ITEM);
  }

  @Test
  public void testAllOfArrayToDirectArrayDetectsChangedItemType() {
    assertOpenApiBackwardIncompatible(ALLOF_ARRAY_INTEGER_ITEM, DIRECT_ARRAY_STRING_ITEM);
  }
}
