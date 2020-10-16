package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class Swagger2CompatibilityTest {
  private final String SWAGGER_DOC1 = "petstore_swagger2.yaml";
  private final String OPENAPI_DOC2 = "petstore_openapi3.yaml";

  @Test
  public void testEqual() {
    assertOpenApiAreEquals(SWAGGER_DOC1, SWAGGER_DOC1);
  }

  @Test
  public void testSwagger2ToOpenapi3() {
    assertOpenApiAreEquals(SWAGGER_DOC1, OPENAPI_DOC2);
  }
}
