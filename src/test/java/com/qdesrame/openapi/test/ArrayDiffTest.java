package com.qdesrame.openapi.test;

import com.qdesrame.openapi.diff.compare.OpenApiDiff;
import com.qdesrame.openapi.diff.model.ChangedEndpoint;
import com.qdesrame.openapi.diff.model.ChangedOpenApi;
import com.qdesrame.openapi.diff.model.Endpoint;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ArrayDiffTest {

    private final String OPENAPI_DOC31 = "array_diff_1.yaml";
    private final String OPENAPI_DOC32 = "array_diff_2.yaml";

    @Test
    public void testArrayDiffDifferent() {
        ChangedOpenApi changedOpenApi = OpenApiDiff.compare(OPENAPI_DOC31, OPENAPI_DOC32);
        List<Endpoint> newEndpoints = changedOpenApi.getNewEndpoints();
        List<Endpoint> missingEndpoints = changedOpenApi.getMissingEndpoints();
        List<ChangedEndpoint> changedEndPoints = changedOpenApi.getChangedEndpoints();

        Assert.assertTrue(newEndpoints.isEmpty());
        Assert.assertTrue(missingEndpoints.isEmpty());
        Assert.assertTrue(changedEndPoints.size() > 0);
    }

    @Test
    public void testArrayDiffSame() {
        ChangedOpenApi changedOpenApi = OpenApiDiff.compare(OPENAPI_DOC31, OPENAPI_DOC31);
        List<Endpoint> newEndpoints = changedOpenApi.getNewEndpoints();
        List<Endpoint> missingEndpoints = changedOpenApi.getMissingEndpoints();
        List<ChangedEndpoint> changedEndPoints = changedOpenApi.getChangedEndpoints();

        Assert.assertTrue(newEndpoints.isEmpty());
        Assert.assertTrue(missingEndpoints.isEmpty());
        Assert.assertTrue(changedEndPoints.isEmpty());
    }

}
