package org.openapitools.openapidiff.core.output;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.OpenApiCompare;
import org.openapitools.openapidiff.core.model.ChangedList;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class MarkdownRenderTest {
  @Test
  public void renderDoesNotFailWhenPropertyHasBeenRemoved() {
    MarkdownRender render = new MarkdownRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("missing_property_1.yaml", "missing_property_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void renderDoesNotCauseStackOverflowWithRecursiveDefinitions() {
    MarkdownRender render = new MarkdownRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff = OpenApiCompare.fromLocations("recursive_old.yaml", "recursive_new.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void renderDoesNotFailWhenHTTPStatusCodeIsRange() {
    MarkdownRender render = new MarkdownRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("range_statuscode_1.yaml", "range_statuscode_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void testEnumRenameDetection() {
    MarkdownRender render = new MarkdownRender();

    // Test 1:1 rename (should show as rename)
    ChangedList<String> enumRename =
        org.openapitools.openapidiff.core.compare.ListDiff.diff(
            new ChangedList.SimpleChangedList<>(
                Collections.singletonList("OldValue"), Collections.singletonList("NewValue")));
    String renameResult = render.listDiff(2, "enum", enumRename);
    assertThat(renameResult).contains("Renamed enum value:");
    assertThat(renameResult).contains("`OldValue` -> `NewValue`");

    // Test multiple additions and removals (should fall back to original behavior)
    ChangedList<String> enumMultiple =
        org.openapitools.openapidiff.core.compare.ListDiff.diff(
            new ChangedList.SimpleChangedList<>(
                Arrays.asList("OldValue1", "OldValue2"), Arrays.asList("NewValue1", "NewValue2")));
    String multipleResult = render.listDiff(2, "enum", enumMultiple);
    assertThat(multipleResult).contains("Added enum values:");
    assertThat(multipleResult).contains("Removed enum values:");

    // Test single addition (should use original behavior)
    ChangedList<String> enumAdd =
        org.openapitools.openapidiff.core.compare.ListDiff.diff(
            new ChangedList.SimpleChangedList<>(
                Collections.emptyList(), Collections.singletonList("NewValue")));
    String addResult = render.listDiff(2, "enum", enumAdd);
    assertThat(addResult).contains("Added enum value:");
    assertThat(addResult).doesNotContain("Renamed");

    // Test single removal (should use original behavior)
    ChangedList<String> enumRemove =
        org.openapitools.openapidiff.core.compare.ListDiff.diff(
            new ChangedList.SimpleChangedList<>(
                Collections.singletonList("OldValue"), Collections.emptyList()));
    String removeResult = render.listDiff(2, "enum", enumRemove);
    assertThat(removeResult).contains("Removed enum value:");
    assertThat(removeResult).doesNotContain("Renamed");
  }
}
