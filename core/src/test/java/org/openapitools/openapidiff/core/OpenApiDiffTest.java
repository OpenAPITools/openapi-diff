package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;
import org.openapitools.openapidiff.core.model.Endpoint;
import org.openapitools.openapidiff.core.output.HtmlRender;
import org.openapitools.openapidiff.core.output.JsonRender;
import org.openapitools.openapidiff.core.output.MarkdownRender;

public class OpenApiDiffTest {

  final String SWAGGER_V2_HTTP = "http://petstore.swagger.io/v2/swagger.json";
  private final String OPENAPI_DOC1 = "petstore_v2_1.yaml";
  private final String OPENAPI_DOC2 = "petstore_v2_2.yaml";
  private final String OPENAPI_EMPTY_DOC = "petstore_v2_empty.yaml";

  @Test
  public void testEqual() {
    assertOpenApiAreEquals(OPENAPI_DOC2, OPENAPI_DOC2);
  }

  @Test
  public void testNewApi() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_EMPTY_DOC, OPENAPI_DOC2);
    List<Endpoint> newEndpoints = changedOpenApi.getNewEndpoints();
    List<Endpoint> missingEndpoints = changedOpenApi.getMissingEndpoints();
    List<ChangedOperation> changedEndPoints = changedOpenApi.getChangedOperations();
    String html =
        new HtmlRender("Changelog", "http://deepoove.com/swagger-diff/stylesheets/demo.css")
            .render(changedOpenApi);

    try {
      FileWriter fw = new FileWriter("target/testNewApi.html");
      fw.write(html);
      fw.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
    assertThat(newEndpoints).isNotEmpty();
    assertThat(missingEndpoints).isEmpty();
    assertThat(changedEndPoints).isEmpty();
  }

  @Test
  public void testDeprecatedApi() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_EMPTY_DOC);
    List<Endpoint> newEndpoints = changedOpenApi.getNewEndpoints();
    List<Endpoint> missingEndpoints = changedOpenApi.getMissingEndpoints();
    List<ChangedOperation> changedEndPoints = changedOpenApi.getChangedOperations();
    String html =
        new HtmlRender("Changelog", "http://deepoove.com/swagger-diff/stylesheets/demo.css")
            .render(changedOpenApi);

    try {
      FileWriter fw = new FileWriter("target/testDeprecatedApi.html");
      fw.write(html);
      fw.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
    assertThat(newEndpoints).isEmpty();
    assertThat(missingEndpoints).isNotEmpty();
    assertThat(changedEndPoints).isEmpty();
  }

  @Test
  public void testDiff() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    List<ChangedOperation> changedEndPoints = changedOpenApi.getChangedOperations();
    String html =
        new HtmlRender("Changelog", "http://deepoove.com/swagger-diff/stylesheets/demo.css")
            .render(changedOpenApi);
    try {
      FileWriter fw = new FileWriter("target/testDiff.html");
      fw.write(html);
      fw.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
    assertThat(changedEndPoints).isNotEmpty();
  }

  @Test
  public void testDiffAndMarkdown() {
    ChangedOpenApi diff = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    String render = new MarkdownRender().render(diff);
    try {
      FileWriter fw = new FileWriter("target/testDiff.md");
      fw.write(render);
      fw.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testDiffAndJson() {
    ChangedOpenApi diff = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    String render = new JsonRender().render(diff);
    try {
      FileWriter fw = new FileWriter("target/testDiff.json");
      fw.write(render);
      fw.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
