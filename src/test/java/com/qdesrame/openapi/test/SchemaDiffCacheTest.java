package com.qdesrame.openapi.test;

import org.junit.Test;

import static com.qdesrame.openapi.test.TestUtils.assertOpenApiAreEquals;

/**
 * Created by adarsh.sharma on 25/12/17.
 */
public class SchemaDiffCacheTest {

    private final String OPENAPI_DOC1 = "schema_diff_cache_1.yaml";

    @Test
    public void testDiffSame() {
        assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
    }
}
