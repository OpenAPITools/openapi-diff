package com.qdesrame.openapi.test;

import com.qdesrame.openapi.diff.OpenApiDiff;
import com.qdesrame.openapi.diff.model.ChangedEndpoint;
import com.qdesrame.openapi.diff.model.Endpoint;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ArrayDiffTest {

    private final String OPENAPI_DOC31 = "array_diff_1.yaml";
    private final String OPENAPI_DOC32 = "array_diff_2.yaml";

    @Test
    public void testArrayDiffDifferent() {
        OpenApiDiff diff = OpenApiDiff.compare(OPENAPI_DOC31, OPENAPI_DOC32);
        List<Endpoint> newEndpoints = diff.getNewEndpoints();
        List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
        List<ChangedEndpoint> changedEndPoints = diff.getChangedEndpoints();

        Assert.assertTrue(newEndpoints.isEmpty());
        Assert.assertTrue(missingEndpoints.isEmpty());
        Assert.assertTrue(changedEndPoints.size() > 0);
    }

    @Test
    public void testArrayDiffSame() {
        OpenApiDiff diff = OpenApiDiff.compare(OPENAPI_DOC31, OPENAPI_DOC31);
        List<Endpoint> newEndpoints = diff.getNewEndpoints();
        List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
        List<ChangedEndpoint> changedEndPoints = diff.getChangedEndpoints();

        Assert.assertTrue(newEndpoints.isEmpty());
        Assert.assertTrue(missingEndpoints.isEmpty());
        Assert.assertTrue(changedEndPoints.isEmpty());
    }

}
