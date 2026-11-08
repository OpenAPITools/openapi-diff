package org.openapitools.openapidiff.maven;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.openapitools.openapidiff.core.compare.OpenApiDiffOptions;

/** Builds the comparison options shared by the {@code diff} and {@code report} goals. */
final class OpenApiDiffOptionsFactory {

  private OpenApiDiffOptionsFactory() {}

  /**
   * Creates the options from the plugin configuration. Config files are applied first and config
   * properties are applied last, so properties override values from the files.
   *
   * @param configFiles YAML config files, may be {@code null}
   * @param configProps config properties, may be {@code null}
   * @return the comparison options
   */
  static OpenApiDiffOptions create(List<File> configFiles, Map<String, String> configProps) {
    OpenApiDiffOptions.Builder builder = OpenApiDiffOptions.builder();
    if (configFiles != null) {
      configFiles.forEach(builder::configYaml);
    }
    if (configProps != null) {
      configProps.forEach(builder::configProperty);
    }
    return builder.build();
  }
}
