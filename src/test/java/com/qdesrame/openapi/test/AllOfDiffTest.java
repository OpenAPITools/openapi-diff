package com.qdesrame.openapi.test;

import com.qdesrame.openapi.diff.compare.OpenApiDiff;
import com.qdesrame.openapi.diff.model.ChangedEndpoint;
import com.qdesrame.openapi.diff.model.ChangedOpenApi;
import com.qdesrame.openapi.diff.model.Endpoint;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by adarsh.sharma on 19/12/17.
 */
public class AllOfDiffTest {

    private final String OPENAPI_DOC1 = "allOf_diff_1.yaml";
    private final String OPENAPI_DOC2 = "allOf_diff_2.yaml";
    private final String OPENAPI_DOC3 = "allOf_diff_3.yaml";

    @Test
    public void testDiffSame() {
        ChangedOpenApi changedOpenApi = OpenApiDiff.compare(OPENAPI_DOC1, OPENAPI_DOC1);
        List<Endpoint> newEndpoints = changedOpenApi.getNewEndpoints();
        List<Endpoint> missingEndpoints = changedOpenApi.getMissingEndpoints();
        List<ChangedEndpoint> changedEndPoints = changedOpenApi.getChangedEndpoints();

        Assert.assertTrue(newEndpoints.isEmpty());
        Assert.assertTrue(missingEndpoints.isEmpty());
        Assert.assertTrue(changedEndPoints.isEmpty());
    }

    @Test
    public void testDiffSameWithAllOf() {
        ChangedOpenApi changedOpenApi = OpenApiDiff.compare(OPENAPI_DOC1, OPENAPI_DOC2);
        List<Endpoint> newEndpoints = changedOpenApi.getNewEndpoints();
        List<Endpoint> missingEndpoints = changedOpenApi.getMissingEndpoints();
        List<ChangedEndpoint> changedEndPoints = changedOpenApi.getChangedEndpoints();

        Assert.assertTrue(newEndpoints.isEmpty());
        Assert.assertTrue(missingEndpoints.isEmpty());
        Assert.assertTrue(changedEndPoints.isEmpty());
    }

    @Test
    public void testDiffDifferent() {
        ChangedOpenApi changedOpenApi  = OpenApiDiff.compare(OPENAPI_DOC1, OPENAPI_DOC3);
        List<Endpoint> newEndpoints = changedOpenApi.getNewEndpoints();
        List<Endpoint> missingEndpoints = changedOpenApi.getMissingEndpoints();
        List<ChangedEndpoint> changedEndPoints = changedOpenApi.getChangedEndpoints();

        Assert.assertTrue(newEndpoints.isEmpty());
        Assert.assertTrue(missingEndpoints.isEmpty());
        Assert.assertTrue(changedEndPoints.size() > 0);
    }

}
