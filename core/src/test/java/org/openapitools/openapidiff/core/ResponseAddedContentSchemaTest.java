package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.media.Content;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedResponse;
import org.openapitools.openapidiff.core.output.ConsoleRender;
import org.openapitools.openapidiff.core.output.HtmlRender;
import org.openapitools.openapidiff.core.output.MarkdownRender;

public class ResponseAddedContentSchemaTest {

  private final String OPENAPI_DOC1 = "response_schema_added_1.yaml";
  private final String OPENAPI_DOC2 = "response_schema_added_2.yaml";

  @Test
  public void testDiffDifferent() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);

    assertThat(changedOpenApi.getNewEndpoints()).isEmpty();
    assertThat(changedOpenApi.getMissingEndpoints()).isEmpty();
    assertThat(changedOpenApi.getChangedOperations()).isNotEmpty();

    Map<String, ChangedResponse> changedResponses =
        changedOpenApi.getChangedOperations().get(0).getApiResponses().getChanged();
    assertThat(changedResponses).containsKey("200");

    ChangedResponse changedResponse = changedResponses.get("200");
    Content oldContent = changedResponse.getOldApiResponse().getContent();
    Content newContent = changedResponse.getNewApiResponse().getContent();
    assertThat(oldContent.get("application/json").getSchema()).isNull();
    assertThat(newContent.get("application/json").getSchema()).isNotNull();
  }

  @Test
  public void testDiffCanBeRendered() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);

    assertThat(new ConsoleRender().render(changedOpenApi)).isNotBlank();
    assertThat(new HtmlRender().render(changedOpenApi)).isNotBlank();
    assertThat(new MarkdownRender().render(changedOpenApi)).isNotBlank();
  }
}
