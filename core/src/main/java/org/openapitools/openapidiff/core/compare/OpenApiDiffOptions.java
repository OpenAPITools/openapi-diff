package org.openapitools.openapidiff.core.compare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class OpenApiDiffOptions {
  private final CompositeConfiguration config;

  private OpenApiDiffOptions(CompositeConfiguration config) {
    this.config = config;
  }

  public CompositeConfiguration getConfig() {
    return config;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private OpenApiDiffOptions built = new OpenApiDiffOptions(new CompositeConfiguration());

    public Builder configYaml(File file) {
      YAMLConfiguration yamlConfig = new YAMLConfiguration();
      try {
        yamlConfig.read(new FileReader(file));
      } catch (ConfigurationException | FileNotFoundException e) {
        throw new IllegalArgumentException("Problem loading config. file=" + file, e);
      }
      // Ideally immutable, but since it isn't, we just modify the config directly
      built.getConfig().addConfigurationFirst(yamlConfig);
      return this;
    }

    public Builder configProperty(String propKey, String propVal) {
      built.getConfig().setProperty(propKey, propVal);
      return this;
    }

    public OpenApiDiffOptions build() {
      return built;
    }
  }
}
