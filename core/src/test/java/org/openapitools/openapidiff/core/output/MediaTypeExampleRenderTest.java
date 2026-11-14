package org.openapitools.openapidiff.core.output;

import static io.swagger.v3.oas.models.PathItem.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.ChangesResolver.getChangedOperation;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.parser.core.models.ParseOptions;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openapitools.openapidiff.core.OpenApiCompare;
import org.openapitools.openapidiff.core.model.ChangedMediaType;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;
import org.openapitools.openapidiff.core.model.DiffResult;

class MediaTypeExampleRenderTest {
  private static final String FIXTURE = "issue-872-media-type-examples.yaml";

  static Stream<Arguments> exampleChanges() {
    return Stream.of("markdown", "console", "asciidoc", "html")
        .flatMap(
            renderer ->
                Stream.of(false, true)
                    .flatMap(
                        response ->
                            Stream.of("example", "examples")
                                .flatMap(
                                    kind ->
                                        Stream.of("added", "removed", "updated")
                                            .map(
                                                change ->
                                                    Arguments.of(
                                                        renderer, response, kind, change)))));
  }

  @ParameterizedTest(name = "{0}: response={1}, {2} {3}")
  @MethodSource("exampleChanges")
  void rendersMediaTypeExampleChangesWithoutSchemaChanges(
      String renderer, boolean response, String kind, String change) {
    OpenAPI oldSpec = readSpec();
    OpenAPI newSpec = readSpec();
    if (!"added".equals(change)) {
      setExample(mediaType(oldSpec, response), kind, "old-message");
    }
    if (!"removed".equals(change)) {
      setExample(mediaType(newSpec, response), kind, "new-message");
    }
    ChangedOpenApi diff = OpenApiCompare.fromSpecifications(oldSpec, newSpec);
    assertThat(diff.isChanged()).isEqualTo(DiffResult.METADATA);
    assertThat(changedMediaType(diff, response).getSchema()).isNull();

    String output = renderer(renderer).render(diff);
    if ("added".equals(change)) {
      assertThat(output).contains("Added " + kind, "new-message").doesNotContain("old-message");
    } else if ("removed".equals(change)) {
      assertThat(output).contains("Removed " + kind, "old-message").doesNotContain("new-message");
    } else {
      assertThat(output).contains("Old " + kind, "New " + kind, "old-message", "new-message");
    }
    if ("examples".equals(kind)) {
      assertThat(output).contains("sample", "Sample payload");
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"markdown", "console", "asciidoc", "html"})
  void rendersExamplesAlongsideSchemaChanges(String renderer) {
    OpenAPI oldSpec = readSpec();
    OpenAPI newSpec = readSpec();
    setExample(mediaType(oldSpec, false), "example", "old-message");
    setExample(mediaType(newSpec, false), "example", "new-message");
    mediaType(newSpec, false).getSchema().addProperty("extra", new StringSchema());
    ChangedOpenApi diff = OpenApiCompare.fromSpecifications(oldSpec, newSpec);
    assertThat(changedMediaType(diff, false).getSchema()).isNotNull();
    assertThat(renderer(renderer).render(diff))
        .contains("Old example", "New example", "old-message", "new-message");
  }

  @ParameterizedTest
  @ValueSource(strings = {"markdown", "console", "asciidoc", "html"})
  void doesNotRenderUnchangedExamplesWhenSchemaChanges(String renderer) {
    OpenAPI oldSpec = readSpec();
    OpenAPI newSpec = readSpec();
    setExample(mediaType(oldSpec, false), "example", "unchanged-message");
    setExample(mediaType(newSpec, false), "example", "unchanged-message");
    mediaType(newSpec, false).getSchema().addProperty("extra", new StringSchema());
    ChangedOpenApi diff = OpenApiCompare.fromSpecifications(oldSpec, newSpec);
    assertThat(changedMediaType(diff, false).getSchema()).isNotNull();
    assertThat(renderer(renderer).render(diff))
        .doesNotContain("Added example", "Removed example", "Old example", "unchanged-message");
  }

  @Test
  void htmlEscapesExampleValuesAndNames() {
    OpenAPI oldSpec = readSpec();
    OpenAPI newSpec = readSpec();
    mediaType(newSpec, false)
        .addExamples(
            "<script>",
            new Example().value(Collections.singletonMap("message", "<script>alert(1)</script>&")));
    String output = new HtmlRender().render(OpenApiCompare.fromSpecifications(oldSpec, newSpec));
    assertThat(output).contains("&lt;script&gt;", "&amp;").doesNotContain("<script>");
  }

  @Test
  void markdownKeepsMarkupInsideJsonCodeBlocks() {
    OpenAPI oldSpec = readSpec();
    OpenAPI newSpec = readSpec();
    setExample(mediaType(newSpec, false), "example", "```\n<script>&");
    String output =
        new MarkdownRender().render(OpenApiCompare.fromSpecifications(oldSpec, newSpec));
    assertThat(output).contains("```json\n", "\\n<script>&", "\n```\n");
    assertThat(output).doesNotContain("\n<script>");
  }

  @Test
  void asciidocKeepsDelimitersInsideJsonStrings() {
    OpenAPI oldSpec = readSpec();
    OpenAPI newSpec = readSpec();
    setExample(mediaType(newSpec, false), "example", "----\ninclude::remote[]");
    String output =
        new AsciidocRender().render(OpenApiCompare.fromSpecifications(oldSpec, newSpec));
    assertThat(output).contains("[source,json]\n----\n", "----\\ninclude::remote[]");
    assertThat(output).doesNotContain("\ninclude::remote[]");
  }

  static Stream<Arguments> serializationFailures() {
    return Stream.of("markdown", "console", "asciidoc", "html")
        .flatMap(
            renderer ->
                Stream.of("example", "examples")
                    .flatMap(
                        kind ->
                            Stream.of("throwing-getter", "self-reference")
                                .map(failure -> Arguments.of(renderer, kind, failure))));
  }

  @ParameterizedTest(name = "{0}: {1}, {2}")
  @MethodSource("serializationFailures")
  void keepsRenderingOtherDetailsWhenAnExampleCannotBeSerialized(
      String renderer, String kind, String failure) {
    OpenAPI oldSpec = readSpec();
    OpenAPI newSpec = readSpec();
    Object invalidValue =
        "throwing-getter".equals(failure) ? new ThrowingExample() : new SelfReferencingExample();
    if ("example".equals(kind)) {
      mediaType(newSpec, false).setExample(invalidValue);
    } else {
      mediaType(newSpec, false).addExamples("invalid", new Example().value(invalidValue));
    }
    oldSpec
        .getPaths()
        .get("/examples")
        .getPost()
        .getRequestBody()
        .getContent()
        .addMediaType("text/plain", new MediaType().schema(new StringSchema()));
    newSpec
        .getPaths()
        .get("/examples")
        .getPost()
        .getRequestBody()
        .getContent()
        .addMediaType(
            "text/plain",
            new MediaType().schema(new StringSchema()).example("valid-media-type-example"));
    setExample(mediaType(oldSpec, true), "example", "old-response-example");
    setExample(mediaType(newSpec, true), "example", "new-response-example");

    ChangedOpenApi diff = OpenApiCompare.fromSpecifications(oldSpec, newSpec);
    assertThat(diff.isChanged()).isEqualTo(DiffResult.METADATA);
    assertThat(renderer(renderer).render(diff))
        .contains(
            "Example could not be serialized as JSON",
            "valid-media-type-example",
            "old-response-example",
            "new-response-example");
  }

  public static class ThrowingExample {
    public String getValue() {
      throw new IllegalStateException("Example getter failed");
    }

    @Override
    public String toString() {
      throw new IllegalStateException("Example toString must not be used as a fallback");
    }
  }

  static Stream<Arguments> invalidCounterparts() {
    return Stream.of("markdown", "console", "asciidoc", "html")
        .flatMap(
            renderer ->
                Stream.of("example", "examples")
                    .flatMap(
                        kind ->
                            Stream.of(false, true)
                                .map(invalidOld -> Arguments.of(renderer, kind, invalidOld))));
  }

  @ParameterizedTest(name = "{0}: {1}, invalid old example={2}")
  @MethodSource("invalidCounterparts")
  void keepsTheSerializableValueWhenTheOtherSideFails(
      String renderer, String kind, boolean invalidOld) {
    OpenAPI oldSpec = readSpec();
    OpenAPI newSpec = readSpec();
    MediaType invalidMediaType = mediaType(invalidOld ? oldSpec : newSpec, false);
    if ("example".equals(kind)) {
      invalidMediaType.setExample(new ThrowingExample());
    } else {
      invalidMediaType.addExamples(
          "sample", new Example().summary("Sample payload").value(new ThrowingExample()));
    }
    setExample(mediaType(invalidOld ? newSpec : oldSpec, false), kind, "valid-counterpart");
    ChangedOpenApi diff = OpenApiCompare.fromSpecifications(oldSpec, newSpec);
    assertThat(diff.isChanged()).isEqualTo(DiffResult.METADATA);
    assertThat(renderer(renderer).render(diff))
        .contains(
            "Old " + kind,
            "New " + kind,
            "Example could not be serialized as JSON",
            "valid-counterpart");
  }

  public static class SelfReferencingExample {
    public SelfReferencingExample getValue() {
      return this;
    }
  }

  private OpenAPI readSpec() {
    return new OpenAPIParser().readLocation(FIXTURE, null, new ParseOptions()).getOpenAPI();
  }

  private MediaType mediaType(OpenAPI spec, boolean response) {
    return (response
            ? spec.getPaths().get("/examples").getPost().getResponses().get("200").getContent()
            : spec.getPaths().get("/examples").getPost().getRequestBody().getContent())
        .get("application/json");
  }

  private void setExample(MediaType mediaType, String kind, String message) {
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("message", message);
    payload.put("enabled", false);
    payload.put("count", 0);
    payload.put("items", Arrays.asList("first", "second"));
    if ("example".equals(kind)) {
      mediaType.setExample(payload);
    } else {
      mediaType.addExamples("sample", new Example().summary("Sample payload").value(payload));
    }
  }

  private ChangedMediaType changedMediaType(ChangedOpenApi diff, boolean response) {
    ChangedOperation operation = getChangedOperation(diff, POST, "/examples");
    assertThat(operation).isNotNull();
    return (response
            ? operation.getApiResponses().getChanged().get("200").getContent()
            : operation.getRequestBody().getContent())
        .getChanged()
        .get("application/json");
  }

  private Render renderer(String name) {
    switch (name) {
      case "markdown":
        return new MarkdownRender();
      case "console":
        return new ConsoleRender();
      case "asciidoc":
        return new AsciidocRender();
      case "html":
        return new HtmlRender();
      default:
        throw new IllegalArgumentException(name);
    }
  }
}
