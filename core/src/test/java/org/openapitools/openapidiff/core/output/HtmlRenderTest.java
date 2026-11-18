package org.openapitools.openapidiff.core.output;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.OpenApiCompare;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class HtmlRenderTest {
  @Test
  public void renderDoesNotFailWhenPropertyHasBeenRemoved() {
    HtmlRender render = new HtmlRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations("missing_property_1.yaml", "missing_property_2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void issue865_renderDoesNotFailWhenSchemaIsNullButExampleChanged() {
    HtmlRender render = new HtmlRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations(
            "issue-865-null-schema-1.yaml", "issue-865-null-schema-2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  @Test
  public void issue865_renderWithShowAllChangesDoesNotFailWhenSchemaIsNullButExampleChanged() {
    HtmlRender render = new HtmlRender(true);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff =
        OpenApiCompare.fromLocations(
            "issue-865-null-schema-1.yaml", "issue-865-null-schema-2.yaml");
    render.render(diff, outputStreamWriter);
    assertThat(outputStream.toString()).isNotBlank();
  }

  private String render(String oldSpec, String newSpec) {
    HtmlRender render = new HtmlRender();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff = OpenApiCompare.fromLocations(oldSpec, newSpec);
    render.render(diff, outputStreamWriter);
    return outputStream.toString();
  }

  private String renderWithAllChanges(String oldSpec, String newSpec) {
    HtmlRender render = new HtmlRender(true);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    ChangedOpenApi diff = OpenApiCompare.fromLocations(oldSpec, newSpec);
    render.render(diff, outputStreamWriter);
    return outputStream.toString();
  }

  @Test
  public void issue215_renderReportsSpecTitleVersionAndResultSection() {
    String html = render("petstore_v2_1.yaml", "petstore_v2_2.yaml");
    assertThat(html).contains("(v ");
    assertThat(html).contains("Result");
    assertThat(html).contains("API changes broke backward compatibility");

    String compatibleHtml = render("petstore_v2_empty.yaml", "petstore_v2_2.yaml");
    assertThat(compatibleHtml).contains("API changes are backward compatible");

    String unchangedHtml = render("petstore_v2_1.yaml", "petstore_v2_1.yaml");
    assertThat(unchangedHtml).contains("No differences. Specifications are equivalent");
  }

  @Test
  public void issue215_renderReportsNewOptionalAndRequiredProperties() {
    String html = render("bc_required_base.yaml", "bc_required_changed_but_compatible.yaml");
    assertThat(html).contains("New optional properties: prop2");
    assertThat(html).contains("New required properties: prop3");
  }

  @Test
  public void issue215_renderReportsEnumValueChanges() {
    String html = render("bc_enum_base.yaml", "bc_enum_changed_but_compatible.yaml");
    assertThat(html).contains("Removed enum value: enum-prop-val-2");
  }

  @Test
  public void issue215_renderReportsAddedAndDeletedProperties() {
    String html = render("missing_property_1.yaml", "missing_property_2.yaml");
    assertThat(html).contains("Deleted property: childProperty");

    String htmlAllChanges =
        renderWithAllChanges("missing_property_1.yaml", "missing_property_2.yaml");
    assertThat(htmlAllChanges).contains("Deleted property: childProperty");
  }

  @Test
  public void issue215_renderReportsChangedPropertyType() {
    String html = render("bc_schema_base.yaml", "bc_schema_type_changed.yaml");
    assertThat(html).contains("Changed property: response_prop1 (integer -&gt; string)");
  }

  @Test
  public void issue215_renderReportsResponseHeaderChanges() {
    String deletedHtml =
        render("bc_response_headers_base.yaml", "bc_response_headers_decreased.yaml");
    assertThat(deletedHtml).contains("Deleted header : X-Header-2");

    String changedHtml =
        render("bc_response_header_base.yaml", "bc_response_header_changed_but_compatible.yaml");
    assertThat(changedHtml).contains("Changed header : X-Header-1");
  }

  @Test
  public void renderDoesNotCauseStackOverflowWithRecursiveDefinitions() {
    String html = render("recursive_old.yaml", "recursive_new.yaml");
    assertThat(html).isNotBlank();
  }
}
