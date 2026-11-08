package org.openapitools.openapidiff.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Locale;
import org.apache.maven.doxia.sink.impl.Xhtml5BaseSink;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.junit.jupiter.api.Test;

class OpenApiDiffReportTest {

  private final File oldSpecFile = new File("src/test/resources/oldspec.yaml");
  private final File newSpecFile = new File("src/test/resources/newspec.yaml");
  private final File configFile = new File("src/test/resources/test-config-file.yaml");
  private final File reportOldSpecFile = new File("src/test/resources/report-oldspec.yaml");
  private final File reportNewSpecFile = new File("src/test/resources/report-newspec.yaml");

  @Test
  void Should_DescribeReport_When_ListedInSite() {
    final OpenApiDiffReport report = new OpenApiDiffReport();

    assertEquals("openapi-diff", report.getOutputName());
    assertEquals("OpenAPI Diff", report.getName(Locale.ENGLISH));
    assertEquals(MavenReport.CATEGORY_PROJECT_REPORTS, report.getCategoryName());
    assertFalse(report.getDescription(Locale.ENGLISH).isEmpty());
  }

  @Test
  void Should_RenderNoDifferences_When_SpecHasNoChanges() throws MavenReportException {
    final String page = render(report(oldSpecFile, oldSpecFile));

    assertTrue(page.contains("No differences. Specifications are equivalent"));
    assertFalse(page.contains("What's Changed"));
  }

  @Test
  void Should_RenderCompatibleResult_When_SpecIsCompatible() throws MavenReportException {
    final String page = render(report(oldSpecFile, newSpecFile));

    assertTrue(page.contains("API changes are backward compatible"));
    assertTrue(page.contains("GET /widgets"));
    assertTrue(page.contains("Changed query-param-1 in query"));
    assertTrue(page.contains("Changed application/json: schema is backward compatible"));
  }

  @Test
  void Should_RenderIncompatibleResult_When_SpecIsIncompatible() throws MavenReportException {
    final String page = render(report(newSpecFile, oldSpecFile));

    assertTrue(page.contains("API changes broke backward compatibility"));
    assertTrue(page.contains("Compatibility: Breaks backward compatibility"));
    assertTrue(page.contains("Changed query-param-1 in query (breaks backward compatibility)"));
    assertTrue(page.contains("Property prop2 is no longer required"));
  }

  @Test
  void Should_ApplyConfiguration_When_IncompatibleChecksDisabled() throws MavenReportException {
    final OpenApiDiffReport report = report(newSpecFile, oldSpecFile);
    report.configFiles = Collections.singletonList(configFile);
    report.configProps =
        Collections.singletonMap("incompatible.request.params.required.increased", "false");

    final String page = render(report);

    assertTrue(page.contains("API changes are backward compatible"));
  }

  @Test
  void Should_RenderEverySection_When_EndpointsAreAddedDeletedDeprecatedAndChanged()
      throws MavenReportException {
    final String page = render(report(reportOldSpecFile, reportNewSpecFile));

    assertTrue(page.contains("Pet Store 1.0.0 (report-oldspec.yaml)"));
    assertTrue(page.contains("Pet Store 2.0.0 (report-newspec.yaml)"));
    assertTrue(page.contains("API changes broke backward compatibility"));

    assertTrue(page.contains("What's New"));
    assertTrue(page.contains("/store/orders"));
    assertTrue(page.contains("What's Deleted"));
    assertTrue(page.contains("What's Deprecated"));
    assertTrue(page.contains("/pets/{petId}"));

    assertTrue(page.contains("What's Changed"));
    assertTrue(page.contains("Operation ID changed from listPets to listAllPets"));
    assertTrue(page.contains("Added offset in query"));
    assertTrue(page.contains("Deprecated limit in query"));
    assertTrue(page.contains("Deleted application/xml"));
    assertTrue(page.contains("Added 400 Bad Request"));
    assertTrue(page.contains("Changed application/json: schema breaks compatibility"));
    assertTrue(page.contains("Changed type of id: integer (int64) -> string"));
    assertTrue(page.contains("Changed type of [n].id: integer (int64) -> string"));
    assertTrue(page.contains("Removed property tag (string)"));
    // the new maxLength only breaks the request body, where clients send the pet
    assertEquals(1, occurrences(page, "Changed maxLength of name"));
  }

  @Test
  void Should_EscapeSpecificationText_When_Rendering() throws MavenReportException {
    final String html = renderHtml(report(reportOldSpecFile, reportNewSpecFile));

    assertTrue(html.contains("Place an &lt;b&gt;order&lt;/b&gt;"));
    assertFalse(html.contains("<b>order</b>"));
  }

  @Test
  void Should_NotGenerateReport_When_SkipIsTrue() {
    final OpenApiDiffReport report = report(oldSpecFile, newSpecFile);
    report.skip = true;

    assertFalse(report.canGenerateReport());
  }

  @Test
  void Should_NotGenerateReport_When_SpecsAreNotConfigured() {
    assertFalse(new OpenApiDiffReport().canGenerateReport());
  }

  @Test
  void Should_MavenReportException_When_MissingNewSpec() {
    final OpenApiDiffReport report = report(oldSpecFile, new File("DOES_NOT_EXIST"));

    assertThrows(MavenReportException.class, () -> render(report));
  }

  @Test
  void Should_HideAbsolutePaths_When_DisplayingLocations() {
    final OpenApiDiffReport report =
        new OpenApiDiffReport() {
          {
            project = new MavenProject();
            project.setFile(new File("pom.xml").getAbsoluteFile());
          }
        };

    assertEquals(
        "https://example.com/openapi.json",
        report.displayLocation("https://example.com/openapi.json"));
    assertEquals(
        "src/test/resources/oldspec.yaml", report.displayLocation(oldSpecFile.getAbsolutePath()));
    assertEquals(
        "openapi.yaml",
        report.displayLocation(
            new File(System.getProperty("java.io.tmpdir"), "openapi.yaml").getAbsolutePath()));
  }

  private static int occurrences(String text, String part) {
    int count = 0;
    for (int index = text.indexOf(part); index >= 0; index = text.indexOf(part, index + 1)) {
      count++;
    }
    return count;
  }

  private static OpenApiDiffReport report(File oldSpec, File newSpec) {
    final OpenApiDiffReport report = new OpenApiDiffReport();
    report.oldSpec = oldSpec.getAbsolutePath();
    report.newSpec = newSpec.getAbsolutePath();
    return report;
  }

  /** Renders the report as XHTML, as the Maven site does. */
  private static String renderHtml(OpenApiDiffReport report) throws MavenReportException {
    final StringWriter writer = new StringWriter();
    report.generate(new Xhtml5BaseSink(writer), Locale.ENGLISH);
    return writer.toString();
  }

  /** Renders the report as the text a reader sees: without tags and with entities unescaped. */
  private static String render(OpenApiDiffReport report) throws MavenReportException {
    return renderHtml(report)
        .replaceAll("<[^>]+>", "")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&amp;", "&");
  }
}
