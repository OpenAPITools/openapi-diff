package com.qdesrame.openapi.test;

import org.junit.Test;

import static com.qdesrame.openapi.test.TestUtils.assertOpenApiAreEquals;
import static com.qdesrame.openapi.test.TestUtils.assertOpenApiChangedEndpoints;

/**
 * Created by adarsh.sharma on 19/12/17.
 */
public class OneOfDiffTest {

    private final String OPENAPI_DOC1 = "oneOf_diff_1.yaml";
    private final String OPENAPI_DOC2 = "oneOf_diff_2.yaml";
    private final String OPENAPI_DOC3 = "oneOf_diff_3.yaml";

    @Test
    public void testDiffSame() {
        assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
    }

    @Test
    public void testDiffDifferentMapping() {
        assertOpenApiChangedEndpoints(OPENAPI_DOC1, OPENAPI_DOC2);
    }

    @Test
    public void testDiffSameWithOneOf() {
        assertOpenApiAreEquals(OPENAPI_DOC2, OPENAPI_DOC3);
    }

}
