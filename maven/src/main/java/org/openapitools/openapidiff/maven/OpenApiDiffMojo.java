package org.openapitools.openapidiff.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.openapitools.openapidiff.core.OpenApiCompare;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.output.ConsoleRender;

/** A Maven Mojo that diffs two OpenAPI specifications and reports on differences. */
@Mojo(name = "diff", defaultPhase = LifecyclePhase.TEST)
public class OpenApiDiffMojo extends AbstractMojo {
  @Parameter(property = "oldSpec")
  String oldSpec;

  @Parameter(property = "newSpec")
  String newSpec;

  @Parameter(property = "failWhenIncompatible", defaultValue = "false")
  Boolean failWhenIncompatible = false;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      final ChangedOpenApi diff = OpenApiCompare.fromLocations(oldSpec, newSpec);
      getLog().info(new ConsoleRender().render(diff));

      if (failWhenIncompatible && !diff.isCompatible()) {
        throw new BackwardIncompatibilityException("The API changes broke backward compatibility");
      }
    } catch (RuntimeException e) {
      throw new MojoExecutionException("Unexpected error", e);
    }
  }
}
