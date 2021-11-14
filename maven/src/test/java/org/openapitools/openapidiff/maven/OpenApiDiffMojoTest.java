package org.openapitools.openapidiff.maven;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;

class OpenApiDiffMojoTest {
  @Test
  void Should_NotThrow_When_SpecHasNoChanges() {
    final String oldSpec = new File("src/test/resources/oldspec.yaml").getAbsolutePath();

    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = oldSpec;
    mojo.newSpec = oldSpec;
    mojo.failOnIncompatible = true;

    assertDoesNotThrow(mojo::execute);
  }

  @Test
  void Should_NotThrow_When_SpecIsCompatible() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = new File("src/test/resources/oldspec.yaml").getAbsolutePath();
    mojo.newSpec = new File("src/test/resources/newspec.yaml").getAbsolutePath();
    mojo.failOnIncompatible = true;

    assertDoesNotThrow(mojo::execute);
  }

  @Test
  void Should_Throw_When_SpecIsDifferent() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = new File("src/test/resources/oldspec.yaml").getAbsolutePath();
    mojo.newSpec = new File("src/test/resources/newspec.yaml").getAbsolutePath();
    mojo.failOnChanged = true;

    assertThrows(ApiChangedException.class, mojo::execute);
  }

  @Test
  void Should_MojoExecutionException_When_MissingOldSpec() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = new File("DOES_NOT_EXIST").getAbsolutePath();
    mojo.newSpec = new File("src/test/resources/newspec.yaml").getAbsolutePath();

    assertThrows(MojoExecutionException.class, mojo::execute);
  }

  @Test
  void Should_MojoExecutionException_When_MissingNewSpec() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = new File("src/test/resources/oldspec.yaml").getAbsolutePath();
    mojo.newSpec = new File("DOES_NOT_EXIST").getAbsolutePath();

    assertThrows(MojoExecutionException.class, mojo::execute);
  }

  @Test
  void Should_NotThrow_When_DefaultsAndSpecIsIncompatible() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = new File("src/test/resources/newspec.yaml").getAbsolutePath();
    mojo.newSpec = new File("src/test/resources/oldspec.yaml").getAbsolutePath();

    assertDoesNotThrow(mojo::execute);
  }

  @Test
  void Should_BackwardIncompatibilityException_When_WantsExceptionAndSpecIsIncompatible() {
    final OpenApiDiffMojo mojo = new OpenApiDiffMojo();
    mojo.oldSpec = new File("src/test/resources/newspec.yaml").getAbsolutePath();
    mojo.newSpec = new File("src/test/resources/oldspec.yaml").getAbsolutePath();
    mojo.failOnIncompatible = true;

    assertThrows(BackwardIncompatibilityException.class, mojo::execute);
  }
}
