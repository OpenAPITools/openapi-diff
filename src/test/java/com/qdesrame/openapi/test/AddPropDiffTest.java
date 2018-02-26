package com.qdesrame.openapi.test;

import org.junit.Test;

import static com.qdesrame.openapi.test.TestUtils.assertOpenApiAreEquals;
import static com.qdesrame.openapi.test.TestUtils.assertOpenApiBackwardIncompatible;

/**
 * Created by adarsh.sharma on 26/02/18.
 */
public class AddPropDiffTest {
    private final String OPENAPI_DOC1 = "add-prop-1.yaml";
    private final String OPENAPI_DOC2 = "add-prop-2.yaml";

    @Test
    public void testDiffSame() {
        assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
    }

    @Test
    public void testDiffDifferent() {
        assertOpenApiBackwardIncompatible(OPENAPI_DOC1, OPENAPI_DOC2);
    }

}
