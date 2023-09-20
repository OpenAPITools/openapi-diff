package org.openapitools.openapidiff.maven;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OpenApiDiffMojoTest {

  private static final Logger logger = LoggerFactory.getLogger(OpenApiDiffMojoTest.class);

  private final File oldSpecFile = new File("src/test/resources/oldspec.yaml");
  private final File newSpecFile = new File("src/test/resources/newspec.yaml");

  private final File consoleOutputfile = new File("target/diff.txt");
  private final File markdownOutputfile = new File("target/diff.md");
  private final File jsonOutputfile = new File("target/diff.json");
  private final File asciidocOutputfile = new File("target/diff.adoc");

  @BeforeEach
  void setup() {
    cleanupGeneratedFiles();
  }

  @AfterEach
  void tearDown() {
    cleanupGeneratedFiles();
  }

  @Test
  void Should_NotThrow_When_SpecHasNoChanges() {
    final String oldSpec = oldSpecFile.getAbsolutePath();

    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = oldSpec;
    mojo.newSpec = oldSpec;
    mojo.failOnIncompatible = true;

    assertDoesNotThrow(mojo::execute);
  }

  @Test
  void Should_NotThrow_When_SpecIsCompatible() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = oldSpecFile.getAbsolutePath();
    mojo.newSpec = newSpecFile.getAbsolutePath();
    mojo.failOnIncompatible = true;

    assertDoesNotThrow(mojo::execute);
  }

  @Test
  void Should_Throw_When_SpecIsDifferent() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = oldSpecFile.getAbsolutePath();
    mojo.newSpec = newSpecFile.getAbsolutePath();
    mojo.failOnChanged = true;

    assertThrows(ApiChangedException.class, mojo::execute);
  }

  @Test
  void Should_MojoExecutionException_When_MissingOldSpec() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = new File("DOES_NOT_EXIST").getAbsolutePath();
    mojo.newSpec = newSpecFile.getAbsolutePath();

    assertThrows(MojoExecutionException.class, mojo::execute);
  }

  @Test
  void Should_MojoExecutionException_When_MissingNewSpec() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = oldSpecFile.getAbsolutePath();
    mojo.newSpec = new File("DOES_NOT_EXIST").getAbsolutePath();

    assertThrows(MojoExecutionException.class, mojo::execute);
  }

  @Test
  void Should_NotThrow_When_DefaultsAndSpecIsIncompatible() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = newSpecFile.getAbsolutePath();
    mojo.newSpec = oldSpecFile.getAbsolutePath();

    assertDoesNotThrow(mojo::execute);
  }

  @Test
  void Should_BackwardIncompatibilityException_When_WantsExceptionAndSpecIsIncompatible() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = newSpecFile.getAbsolutePath();
    mojo.newSpec = oldSpecFile.getAbsolutePath();
    mojo.failOnIncompatible = true;

    assertThrows(BackwardIncompatibilityException.class, mojo::execute);
  }

  @Test
  void Should_Skip_Mojo_WhenSkipIsTrue() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = newSpecFile.getAbsolutePath();
    mojo.newSpec = oldSpecFile.getAbsolutePath();
    mojo.failOnIncompatible = true;
    mojo.skip = true;

    assertDoesNotThrow(mojo::execute);
  }

  @Test
  void Should_outputToTextFile_When_SpecIsDifferent() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = oldSpecFile.getAbsolutePath();
    mojo.newSpec = newSpecFile.getAbsolutePath();

    mojo.consoleOutputFileName = consoleOutputfile.getAbsolutePath();
    mojo.failOnChanged = true;

    assertThrows(ApiChangedException.class, mojo::execute);

    assertTrue(Files.exists(consoleOutputfile.toPath()));
  }

  @Test
  void Should_outputToMarkdownFile_When_SpecIsDifferent() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = oldSpecFile.getAbsolutePath();
    mojo.newSpec = newSpecFile.getAbsolutePath();

    mojo.markdownOutputFileName = markdownOutputfile.getAbsolutePath();
    mojo.failOnChanged = true;

    assertThrows(ApiChangedException.class, mojo::execute);

    assertTrue(Files.exists(markdownOutputfile.toPath()));
  }

  @Test
  void Should_outputToJsonFile_When_SpecIsDifferent() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = oldSpecFile.getAbsolutePath();
    mojo.newSpec = newSpecFile.getAbsolutePath();

    mojo.jsonOutputFileName = jsonOutputfile.getAbsolutePath();
    mojo.failOnChanged = true;

    assertThrows(ApiChangedException.class, mojo::execute);

    assertTrue(Files.exists(jsonOutputfile.toPath()));
  }

  @Test
  void Should_outputToAsccidocFile_When_SpecIsDifferent() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = oldSpecFile.getAbsolutePath();
    mojo.newSpec = newSpecFile.getAbsolutePath();

    mojo.asciidocOutputFileName = asciidocOutputfile.getAbsolutePath();
    mojo.failOnChanged = true;

    assertThrows(ApiChangedException.class, mojo::execute);

    assertTrue(Files.exists(asciidocOutputfile.toPath()));
  }

  private void cleanupGeneratedFiles() {
    try {
      Files.deleteIfExists(Paths.get(consoleOutputfile.getPath()));
    } catch (IOException ioException) {
      logger.warn("Exception while trying to delete file {}", consoleOutputfile.getAbsolutePath());
    }

    try {
      Files.deleteIfExists(Paths.get(markdownOutputfile.getPath()));
    } catch (IOException ioException) {
      logger.warn("Exception while trying to delete file {}", markdownOutputfile.getAbsolutePath());
    }

    try {
      Files.deleteIfExists(Paths.get(jsonOutputfile.getPath()));
    } catch (IOException ioException) {
      logger.warn("Exception while trying to delete file {}", jsonOutputfile.getAbsolutePath());
    }
  }
}
