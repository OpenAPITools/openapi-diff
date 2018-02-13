package com.qdesrame.openapi.test;

import org.junit.Test;

import static com.qdesrame.openapi.test.TestUtils.assertOpenApiAreEquals;
import static com.qdesrame.openapi.test.TestUtils.assertOpenApiBackwardIncompatible;

/**
 * Created by adarsh.sharma on 13/02/18.
 */
public class RecursiveSchemaTest {

    private final String OPENAPI_DOC1 = "recursive_model_1.yaml";
    private final String OPENAPI_DOC2 = "recursive_model_2.yaml";

    @Test
    public void testDiffSame() {
        assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
    }

    @Test
    public void testDiffDifferent() {
        assertOpenApiBackwardIncompatible(OPENAPI_DOC1, OPENAPI_DOC2);
    }
}
