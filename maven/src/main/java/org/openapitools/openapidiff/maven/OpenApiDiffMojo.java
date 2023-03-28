package org.openapitools.openapidiff.maven;

import java.io.FileWriter;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.openapitools.openapidiff.core.OpenApiCompare;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.output.ConsoleRender;
import org.openapitools.openapidiff.core.output.MarkdownRender;

/** A Maven Mojo that diffs two OpenAPI specifications and reports on differences. */
@Mojo(name = "diff", defaultPhase = LifecyclePhase.TEST)
public class OpenApiDiffMojo extends AbstractMojo {
  @Parameter(property = "oldSpec")
  String oldSpec;

  @Parameter(property = "newSpec")
  String newSpec;

  @Parameter(property = "failOnIncompatible", defaultValue = "false")
  Boolean failOnIncompatible = false;

  @Parameter(property = "failOnChanged", defaultValue = "false")
  Boolean failOnChanged = false;

  @Parameter(property = "skip", defaultValue = "false")
  Boolean skip = false;

  @Parameter(property = "consoleOutputFileName", defaultValue = "")
  String consoleOutputFileName;

  @Parameter(property = "jsonOutputFileName", defaultValue = "")
  String jsonOutputFileName;

  @Parameter(property = "markdownOutputFileName", defaultValue = "")
  String markdownOutputFileName;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (Boolean.TRUE.equals(skip)) {
      getLog().info("Skipping openapi-diff execution");
      return;
    }

    try {
      final ChangedOpenApi diff = OpenApiCompare.fromLocations(oldSpec, newSpec);

      generateConsoleOutput(diff);
      generateJson(diff);
      generateMarkdown(diff);

      if (failOnIncompatible && diff.isIncompatible()) {
        throw new BackwardIncompatibilityException("The API changes broke backward compatibility");
      }

      if (failOnChanged && diff.isDifferent()) {
        throw new ApiChangedException("The API changed");
      }
    } catch (RuntimeException e) {
      throw new MojoExecutionException("Unexpected error", e);
    }
  }

  private void generateJson(final ChangedOpenApi diff) {
    if (isValidFileName(jsonOutputFileName)) {
      final String render = new MarkdownRender().render(diff);
      writeToFile(render, jsonOutputFileName);
    }
  }

  private void generateConsoleOutput(final ChangedOpenApi diff) {
    final String render = new ConsoleRender().render(diff);
    if (isValidFileName(consoleOutputFileName)) {
      writeToFile(render, consoleOutputFileName);
    }
    getLog().info(render);
  }

  private void generateMarkdown(final ChangedOpenApi diff) {
    if (isValidFileName(markdownOutputFileName)) {
      final String render = new MarkdownRender().render(diff);
      writeToFile(render, markdownOutputFileName);
    }
  }

  private boolean isValidFileName(String fileName) {
    return fileName != null && !fileName.isEmpty();
  }

  private static void writeToFile(final String render, final String filename) {
    try {
      final FileWriter fileWriter = new FileWriter(filename);
      fileWriter.write(render);
      fileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
