package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedHeaders;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedResponse;

/** Created by adarsh.sharma on 28/12/17. */
public class ResponseHeaderDiffTest {

  private final String OPENAPI_DOC1 = "header_1.yaml";
  private final String OPENAPI_DOC2 = "header_2.yaml";

  @Test
  public void testDiffDifferent() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);

    assertThat(changedOpenApi.getNewEndpoints()).isEmpty();
    assertThat(changedOpenApi.getMissingEndpoints()).isEmpty();
    assertThat(changedOpenApi.getChangedOperations()).isNotEmpty();

    Map<String, ChangedResponse> changedResponses =
        changedOpenApi.getChangedOperations().get(0).getApiResponses().getChanged();
    assertThat(changedResponses).isNotEmpty();
    assertThat(changedResponses).containsKey("200");
    ChangedHeaders changedHeaders = changedResponses.get("200").getHeaders();
    assertThat(changedHeaders.isDifferent()).isTrue();
    assertThat(changedHeaders.getChanged()).hasSize(1);
    assertThat(changedHeaders.getIncreased()).hasSize(1);
    assertThat(changedHeaders.getMissing()).hasSize(1);
  }
}
