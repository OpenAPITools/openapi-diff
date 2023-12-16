package org.openapitools.openapidiff.maven;

import static org.openapitools.openapidiff.core.utils.FileUtils.writeToFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.openapitools.openapidiff.core.OpenApiCompare;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.output.AsciidocRender;
import org.openapitools.openapidiff.core.output.ConsoleRender;
import org.openapitools.openapidiff.core.output.JsonRender;
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

  @Parameter(property = "consoleOutputFileName")
  String consoleOutputFileName;

  @Parameter(property = "jsonOutputFileName")
  String jsonOutputFileName;

  @Parameter(property = "markdownOutputFileName")
  String markdownOutputFileName;

  @Parameter(property = "asciidocOutputFileName")
  String asciidocOutputFileName;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (Boolean.TRUE.equals(skip)) {
      getLog().info("Skipping openapi-diff execution");
      return;
    }

    try {
      final ChangedOpenApi diff = OpenApiCompare.fromLocations(oldSpec, newSpec);

      try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
          OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
        new ConsoleRender().render(diff, outputStreamWriter);
        getLog().info(outputStream.toString());
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }

      writeDiffAsTextToFile(diff);
      writeDiffAsJsonToFile(diff);
      writeDiffAsMarkdownToFile(diff);
      writeDiffAsAsciidocToFile(diff);

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

  private void writeDiffAsTextToFile(final ChangedOpenApi diff) {
    writeToFile(new ConsoleRender(), diff, consoleOutputFileName);
  }

  private void writeDiffAsJsonToFile(final ChangedOpenApi diff) {
    writeToFile(new JsonRender(), diff, jsonOutputFileName);
  }

  private void writeDiffAsMarkdownToFile(final ChangedOpenApi diff) {
    writeToFile(new MarkdownRender(), diff, markdownOutputFileName);
  }

  private void writeDiffAsAsciidocToFile(final ChangedOpenApi diff) {
    writeToFile(new AsciidocRender(), diff, asciidocOutputFileName);
  }
}
