package com.qdesrame.openapi.test;

import com.qdesrame.openapi.diff.OpenApiCompare;
import com.qdesrame.openapi.diff.model.ChangedOpenApi;
import org.junit.Assert;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class TestUtils {
    public static final Logger LOG = getLogger(TestUtils.class);

    public static void assertOpenApiAreEquals(String oldSpec, String newSpec) {
        ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
        LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
        Assert.assertTrue(changedOpenApi.getNewEndpoints().isEmpty());
        Assert.assertTrue(changedOpenApi.getMissingEndpoints().isEmpty());
        Assert.assertTrue(changedOpenApi.getChangedOperations().isEmpty());
    }

    public static void assertOpenApiChangedEndpoints(String oldSpec, String newSpec) {
        ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
        LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
        Assert.assertTrue(changedOpenApi.getNewEndpoints().isEmpty());
        Assert.assertTrue(changedOpenApi.getMissingEndpoints().isEmpty());
        Assert.assertTrue(changedOpenApi.getChangedOperations().size() > 0);
    }

    public static void assertOpenApiBackwardCompatible(String oldSpec, String newSpec, boolean isDiff) {
        ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
        LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
        Assert.assertTrue(changedOpenApi.isCompatible());
    }

    public static void assertOpenApiBackwardIncompatible(String oldSpec, String newSpec) {
        ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
        LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
        Assert.assertTrue(changedOpenApi.isIncompatible());
    }
}
