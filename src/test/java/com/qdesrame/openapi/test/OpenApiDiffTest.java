package com.qdesrame.openapi.test;

import static com.qdesrame.openapi.test.TestUtils.assertOpenApiAreEquals;
import static org.assertj.core.api.Assertions.assertThat;

import com.qdesrame.openapi.diff.OpenApiCompare;
import com.qdesrame.openapi.diff.model.ChangedOpenApi;
import com.qdesrame.openapi.diff.model.ChangedOperation;
import com.qdesrame.openapi.diff.model.Endpoint;
import com.qdesrame.openapi.diff.output.HtmlRender;
import com.qdesrame.openapi.diff.output.MarkdownRender;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

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
    String html = new HtmlRender("Changelog", "/diff.css").render(changedOpenApi);

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
    String html = new HtmlRender("Changelog", "/diff.css").render(changedOpenApi);

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
    String html = new HtmlRender("Changelog", "/diff.css").render(changedOpenApi);
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
}
