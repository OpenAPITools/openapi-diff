package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;
import org.openapitools.openapidiff.core.model.Endpoint;
import org.openapitools.openapidiff.core.output.HtmlRender;
import org.openapitools.openapidiff.core.output.JsonRender;
import org.openapitools.openapidiff.core.output.MarkdownRender;

public class OpenApiDiffTest {

  private final String OPENAPI_DOC1 = "petstore_v2_1.yaml";
  private final String OPENAPI_DOC2 = "petstore_v2_2.yaml";
  private final String OPENAPI_EMPTY_DOC = "petstore_v2_empty.yaml";

  @Test
  public void testEqual() {
    assertOpenApiAreEquals(OPENAPI_DOC2, OPENAPI_DOC2);
  }

  @Test
  public void testNewApi(@TempDir Path tempDir) throws IOException {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_EMPTY_DOC, OPENAPI_DOC2);
    List<Endpoint> newEndpoints = changedOpenApi.getNewEndpoints();
    List<Endpoint> missingEndpoints = changedOpenApi.getMissingEndpoints();
    List<ChangedOperation> changedEndPoints = changedOpenApi.getChangedOperations();
    assertThat(newEndpoints).isNotEmpty();
    assertThat(missingEndpoints).isEmpty();
    assertThat(changedEndPoints).isEmpty();

    String html =
        new HtmlRender("Changelog", "http://deepoove.com/swagger-diff/stylesheets/demo.css")
            .render(changedOpenApi);
    final Path path = tempDir.resolve("testNewApi.html");
    try (FileWriter fw = new FileWriter(path.toFile())) {
      fw.write(html);
    }
    assertThat(path).isNotEmptyFile();
  }

  @Test
  public void testDeprecatedApi(@TempDir Path tempDir) throws IOException {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_EMPTY_DOC);
    List<Endpoint> newEndpoints = changedOpenApi.getNewEndpoints();
    List<Endpoint> missingEndpoints = changedOpenApi.getMissingEndpoints();
    List<ChangedOperation> changedEndPoints = changedOpenApi.getChangedOperations();
    assertThat(newEndpoints).isEmpty();
    assertThat(missingEndpoints).isNotEmpty();
    assertThat(changedEndPoints).isEmpty();

    String html =
        new HtmlRender("Changelog", "http://deepoove.com/swagger-diff/stylesheets/demo.css")
            .render(changedOpenApi);
    final Path path = tempDir.resolve("testDeprecatedApi.html");
    try (FileWriter fw = new FileWriter(path.toFile())) {
      fw.write(html);
    }
    assertThat(path).isNotEmptyFile();
  }

  @Test
  public void testDiff(@TempDir Path tempDir) throws IOException {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    List<ChangedOperation> changedEndPoints = changedOpenApi.getChangedOperations();
    assertThat(changedEndPoints).isNotEmpty();

    String html =
        new HtmlRender("Changelog", "http://deepoove.com/swagger-diff/stylesheets/demo.css")
            .render(changedOpenApi);
    final Path path = tempDir.resolve("testDiff.html");
    try (FileWriter fw = new FileWriter(path.toFile())) {
      fw.write(html);
    }
    assertThat(path).isNotEmptyFile();
  }

  @Test
  public void testDiffAndMarkdown(@TempDir Path tempDir) throws IOException {
    ChangedOpenApi diff = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    String render = new MarkdownRender().render(diff);
    final Path path = tempDir.resolve("testDiff.md");
    try (FileWriter fw = new FileWriter(path.toFile())) {
      fw.write(render);
    }
    assertThat(path).isNotEmptyFile();
  }

  @Test
  public void testDiffAndJson(@TempDir Path tempDir) throws IOException {
    ChangedOpenApi diff = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    String render = new JsonRender().render(diff);
    final Path path = tempDir.resolve("testDiff.json");
    try (FileWriter fw = new FileWriter(path.toFile())) {
      fw.write(render);
    }
    assertThat(path).isNotEmptyFile();
  }
}
