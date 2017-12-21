package com.qdesrame.openapi.test;

import com.qdesrame.openapi.diff.OpenApiDiff;
import com.qdesrame.openapi.diff.model.ChangedEndpoint;
import com.qdesrame.openapi.diff.model.Endpoint;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by adarsh.sharma on 19/12/17.
 */
public class OneOfDiffTest {

    private final String OPENAPI_DOC1 = "oneOf_diff_1.yaml";
    private final String OPENAPI_DOC2 = "oneOf_diff_2.yaml";
    private final String OPENAPI_DOC3 = "oneOf_diff_3.yaml";

    @Test
    public void testDiffSame() {
        OpenApiDiff diff = OpenApiDiff.compare(OPENAPI_DOC1, OPENAPI_DOC1);
        List<Endpoint> newEndpoints = diff.getNewEndpoints();
        List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
        List<ChangedEndpoint> changedEndPoints = diff.getChangedEndpoints();

        Assert.assertTrue(newEndpoints.isEmpty());
        Assert.assertTrue(missingEndpoints.isEmpty());
        Assert.assertTrue(changedEndPoints.isEmpty());
    }

    @Test
    public void testDiffDifferentMapping() {
        OpenApiDiff diff = OpenApiDiff.compare(OPENAPI_DOC1, OPENAPI_DOC2);
        List<Endpoint> newEndpoints = diff.getNewEndpoints();
        List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
        List<ChangedEndpoint> changedEndPoints = diff.getChangedEndpoints();

        Assert.assertTrue(newEndpoints.isEmpty());
        Assert.assertTrue(missingEndpoints.isEmpty());
        Assert.assertTrue(changedEndPoints.size() > 0);
    }

    @Test
    public void testDiffSameWithOneOf() {
        OpenApiDiff diff = OpenApiDiff.compare(OPENAPI_DOC2, OPENAPI_DOC3);
        List<Endpoint> newEndpoints = diff.getNewEndpoints();
        List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
        List<ChangedEndpoint> changedEndPoints = diff.getChangedEndpoints();

        Assert.assertTrue(newEndpoints.isEmpty());
        Assert.assertTrue(missingEndpoints.isEmpty());
        Assert.assertTrue(changedEndPoints.isEmpty());
    }

}
