package org.openapitools.openapidiff.core.model;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

import org.apache.commons.configuration2.Configuration;

// Properties to enable/disable backward incompatibility checks
// Note: order is not programmatically significant but keep alphabetical for maintainability
public enum BackwardIncompatibleProp {
  REQUEST_ENUM_DECREASED("incompatible.request.enum.decreased", true),
  REQUEST_MAX_LENGTH_DECREASED("incompatible.request.max.length.decreased", true),
  REQUEST_REQUIRED_INCREASED("incompatible.request.required.increased", true),
  RESPONSE_ENUM_INCREASED("incompatible.response.enum.increased", true),
  RESPONSE_MAX_LENGTH_INCREASED("incompatible.response.max.length.increased", true),
  RESPONSE_REQUIRED_DECREASED("incompatible.response.required.decreased", true),
  RESPONSE_RESPONSES_DECREASED("incompatible.response.responses.decreased", true),
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
