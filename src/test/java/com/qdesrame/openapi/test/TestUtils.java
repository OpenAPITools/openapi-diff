package com.qdesrame.openapi.test;

import com.qdesrame.openapi.diff.compare.OpenApiDiff;
import com.qdesrame.openapi.diff.model.ChangedOpenApi;
import org.junit.Assert;

public class TestUtils {

    public static void assertOpenApiAreEquals(String oldSpec, String newSpec) {
        ChangedOpenApi changedOpenApi = OpenApiDiff.compare(oldSpec, newSpec);

        Assert.assertTrue(changedOpenApi.getNewEndpoints().isEmpty());
        Assert.assertTrue(changedOpenApi.getMissingEndpoints().isEmpty());
        Assert.assertTrue(changedOpenApi.getChangedEndpoints().isEmpty());
    }

    public static void assertOpenApiChangedEndpoints(String oldSpec, String newSpec) {
        ChangedOpenApi changedOpenApi = OpenApiDiff.compare(oldSpec, newSpec);

        Assert.assertTrue(changedOpenApi.getNewEndpoints().isEmpty());
        Assert.assertTrue(changedOpenApi.getMissingEndpoints().isEmpty());
        Assert.assertTrue(changedOpenApi.getChangedEndpoints().size() > 0);
    }
}
