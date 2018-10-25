package com.qdesrame.openapi.test;

import com.qdesrame.openapi.diff.OpenApiCompare;
import com.qdesrame.openapi.diff.model.ChangedHeaders;
import com.qdesrame.openapi.diff.model.ChangedOpenApi;
import com.qdesrame.openapi.diff.model.ChangedResponse;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/** Created by adarsh.sharma on 28/12/17. */
public class ResponseHeaderDiffTest {

  private final String OPENAPI_DOC1 = "header_1.yaml";
  private final String OPENAPI_DOC2 = "header_2.yaml";

  @Test
  public void testDiffDifferent() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);

    Assert.assertTrue(changedOpenApi.getNewEndpoints().isEmpty());
    Assert.assertTrue(changedOpenApi.getMissingEndpoints().isEmpty());
    Assert.assertTrue(changedOpenApi.getChangedOperations().size() > 0);

    Map<String, ChangedResponse> changedResponses =
        changedOpenApi.getChangedOperations().get(0).getChangedApiResponse().getChangedResponses();
    Assert.assertTrue(changedResponses.size() > 0);
    Assert.assertTrue(changedResponses.containsKey("200"));
    ChangedHeaders changedHeaders = changedResponses.get("200").getChangedHeaders();
    Assert.assertTrue(changedHeaders.isDifferent());
    Assert.assertTrue(changedHeaders.getChanged().size() == 1);
    Assert.assertTrue(changedHeaders.getIncreased().size() == 1);
    Assert.assertTrue(changedHeaders.getMissing().size() == 1);
  }
}
