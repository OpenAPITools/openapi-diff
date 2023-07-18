package org.openapitools.openapidiff.core.model;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

import org.apache.commons.configuration2.Configuration;

// Properties to enable/disable backward incompatibility checks
// Note: order is not programmatically significant but keep alphabetical for maintainability
public enum BackwardIncompatibleProp {
  REQUEST_ENUM_DECREASED("incompatible.request.enum.decreased", true),
  RESPONSE_ENUM_INCREASED("incompatible.response.enum.increased", true),
  ;

  private final String propertyName;
  private final boolean enabledByDefault;

  BackwardIncompatibleProp(String propertyName, boolean enabledByDefault) {
    this.propertyName = propertyName;
    this.enabledByDefault = enabledByDefault;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public boolean isEnabledByDefault() {
    return enabledByDefault;
  }

  public boolean enabled(DiffContext context, Object... formatArgs) {
    return enabled(context.getConfig(), formatArgs);
  }

  public boolean enabled(Configuration cfg, Object... formatArgs) {
    String propName = isEmpty(formatArgs) ? propertyName : String.format(propertyName, formatArgs);
    return cfg.getBoolean(propName, enabledByDefault);
  }
}
