package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;

import org.junit.jupiter.api.Test;

public class NestedSchemaRefTest {

  private final String OPENAPI_DOC1 = "nested_schema_ref_1.yaml";

  @Test
  public void shouldNotThrowErrorsForNestedRefs() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
  }
}
