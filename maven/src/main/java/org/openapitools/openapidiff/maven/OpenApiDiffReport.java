package org.openapitools.openapidiff.maven;

import java.io.File;
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
              oldSpec, newSpec, e.getMessage()),
          e);
    }
    new OpenApiDiffReportRenderer(
            getSink(), diff, displayLocation(oldSpec), displayLocation(newSpec))
        .render();
  }

  /**
   * Returns the location to show on the page. URLs are shown as they are, files below the project
   * directory relative to it and other files by name only, so a published site does not expose
   * absolute paths of the build machine.
   */
  String displayLocation(String location) {
    if (location.contains("://")) {
      return location;
    }
    try {
      Path path = Paths.get(location).toAbsolutePath().normalize();
      MavenProject project = getProject();
      if (project != null && project.getBasedir() != null) {
        Path basedir = project.getBasedir().toPath().toAbsolutePath().normalize();
        if (path.startsWith(basedir)) {
          return basedir.relativize(path).toString().replace(File.separatorChar, '/');
        }
      }
      Path fileName = path.getFileName();
      return fileName != null ? fileName.toString() : location;
    } catch (InvalidPathException e) {
      return location;
    }
  }

  private static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
