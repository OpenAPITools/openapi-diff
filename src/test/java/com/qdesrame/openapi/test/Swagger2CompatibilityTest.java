package com.qdesrame.openapi.test;

import org.junit.Test;

import static com.qdesrame.openapi.test.TestUtils.assertOpenApiAreEquals;

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
