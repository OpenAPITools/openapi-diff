package org.openapitools.openapidiff.maven;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.openapitools.openapidiff.core.OpenApiCompare;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

/**
 * Adds an "OpenAPI Diff" page to the Maven site, listing the differences between two OpenAPI
 * specifications and whether they are backward compatible.
 *
 * <p>Configure the plugin in the {@code <reporting>} section of the POM to get the page under
 * "Project Reports" when running {@code mvn site}. Unlike the {@code diff} goal, this goal never
 * fails the build because of API changes: it only documents them.
 */
@Mojo(name = "report", defaultPhase = LifecyclePhase.SITE)
public class OpenApiDiffReport extends AbstractMavenReport {

  /** Location of the reference specification (file path or URL), e.g. the released API. */
  @Parameter(property = "oldSpec")
  String oldSpec;

  /** Location of the specification to compare with {@code oldSpec} (file path or URL). */
  @Parameter(property = "newSpec")
  String newSpec;

  /** Config files to override the default incompatibility checks. Later files win. */
  @Parameter(property = "configFiles")
  List<File> configFiles;

  /** Config properties to override the default incompatibility checks and config files. */
  @Parameter(property = "configProps")
  Map<String, String> configProps;

  /** Skips the report. */
  @Parameter(property = "skip", defaultValue = "false")
  Boolean skip = false;

  @Override
  public String getOutputName() {
    return "openapi-diff";
  }

  @Override
  public String getName(Locale locale) {
    return OpenApiDiffReportRenderer.TITLE;
  }

  @Override
  public String getDescription(Locale locale) {
    return "Changes between two OpenAPI specifications and whether they are backward compatible.";
  }

  @Override
  public boolean canGenerateReport() {
    if (Boolean.TRUE.equals(skip)) {
      getLog().info("Skipping openapi-diff report");
      return false;
    }
    if (isBlank(oldSpec) || isBlank(newSpec)) {
      getLog().warn("Skipping openapi-diff report: both oldSpec and newSpec must be configured");
      return false;
    }
    return true;
  }

  @Override
  protected void executeReport(Locale locale) throws MavenReportException {
    final ChangedOpenApi diff;
    try {
      diff =
          OpenApiCompare.fromLocations(
              oldSpec, newSpec, null, OpenApiDiffOptionsFactory.create(configFiles, configProps));
    } catch (RuntimeException e) {
      throw new MavenReportException(
          String.format(
              "Unable to compare %s with %s: %s. If a specification is generated during the"
                  + " build, generate the site with 'mvn verify site'.",
              displayLocation(oldSpec), displayLocation(newSpec), e.getMessage()),
          e);
    }
    new OpenApiDiffReportRenderer(
            getSink(), diff, displayLocation(oldSpec), displayLocation(newSpec))
        .render();
  }

  /**
   * Returns the location to show on the page without exposing the build machine or secrets. Files,
   * including {@code file:} URIs, are shown relative to the project directory, or by name when they
   * are outside of it. URLs are shown without user info, query and fragment, which may hold
   * credentials.
   */
  String displayLocation(String location) {
    URI uri = toUri(location);
    if (uri != null && "file".equalsIgnoreCase(uri.getScheme())) {
      try {
        return displayPath(Paths.get(uri));
      } catch (IllegalArgumentException e) {
        // e.g. an opaque URI such as file:relative.yaml
        return lastSegment(location);
      }
    }
    if (uri != null && uri.getHost() != null) {
      String port = uri.getPort() != -1 ? ":" + uri.getPort() : "";
      String path = uri.getRawPath() != null ? uri.getRawPath() : "";
      return uri.getScheme() + "://" + uri.getHost() + port + path;
    }
    try {
      return displayPath(Paths.get(location));
    } catch (InvalidPathException e) {
      return lastSegment(location);
    }
  }

  private String displayPath(Path location) {
    Path path = location.toAbsolutePath().normalize();
    MavenProject project = getProject();
    if (project != null && project.getBasedir() != null) {
      Path basedir = project.getBasedir().toPath().toAbsolutePath().normalize();
      if (path.startsWith(basedir)) {
        return basedir.relativize(path).toString().replace(File.separatorChar, '/');
      }
    }
    Path fileName = path.getFileName();
    return fileName != null ? fileName.toString() : "";
  }

  /** Parses a location as a URI with a scheme, or returns {@code null} for plain paths. */
  private static URI toUri(String location) {
    try {
      URI uri = new URI(location);
      // a one-letter scheme is a Windows drive, as in C:/specs/openapi.yaml
      return uri.getScheme() != null && uri.getScheme().length() > 1 ? uri : null;
    } catch (URISyntaxException e) {
      return null;
    }
  }

  /** Returns the part after the last slash, without query and fragment. */
  private static String lastSegment(String location) {
    String withoutQuery = location.split("[?#]", 2)[0];
    int separator = Math.max(withoutQuery.lastIndexOf('/'), withoutQuery.lastIndexOf('\\'));
    return withoutQuery.substring(separator + 1);
  }

  private static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
