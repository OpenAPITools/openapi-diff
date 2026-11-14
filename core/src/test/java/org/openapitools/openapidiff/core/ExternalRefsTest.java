package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;

import org.junit.jupiter.api.Test;

public class ExternalRefsTest {
  @Test
  public void testDiffSame() {
    assertOpenApiAreEquals(
        "issue-464/source/specification/openapi.yaml",
        "issue-464/destination/specification/openapi.yaml");
  }
}
