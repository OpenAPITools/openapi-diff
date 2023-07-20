package org.openapitools.openapidiff.core.model;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

import org.apache.commons.configuration2.Configuration;

// Properties to enable/disable backward incompatibility checks
// Note: order is not programmatically significant but keep alphabetical for maintainability
public enum BackwardIncompatibleProp {
  EXTENSION_CONTENT_TYPES_DECREASED("incompatible.extension.content.types.decreased", false),
  EXTENSION_CONTENT_TYPE_DELETED("incompatible.extension.content.type.%s.deleted", false),
  REQUEST_CONTENT_DECREASED("incompatible.request.content.decreased", true),
  REQUEST_ENUM_DECREASED("incompatible.request.enum.decreased", true),
  REQUEST_MAX_LENGTH_DECREASED("incompatible.request.max.length.decreased", true),
  REQUEST_NUMERIC_RANGE_DECREASED("incompatible.request.numeric.range.decreased", true),
  REQUEST_READONLY_INCREASED("incompatible.request.readonly.increased", true),
  REQUEST_READONLY_REQUIRED_DECREASED("incompatible.request.readonly.required.decreased", true),
  REQUEST_REQUIRED_INCREASED("incompatible.request.required.increased", true),
  RESPONSE_CONTENT_DECREASED("incompatible.response.content.decreased", true),
  RESPONSE_ENUM_INCREASED("incompatible.response.enum.increased", true),
  RESPONSE_HEADER_EXPLODE_CHANGED("incompatible.response.header.explode.changed", true),
  RESPONSE_HEADER_REQUIRED_DECREASED("incompatible.response.header.required.decreased", true),
  RESPONSE_HEADER_REQUIRED_INCREASED("incompatible.response.header.required.increased", true),
  RESPONSE_HEADERS_DECREASED("incompatible.response.headers.decreased", true),
  RESPONSE_MAX_LENGTH_INCREASED("incompatible.response.max.length.increased", true),
  RESPONSE_NUMERIC_RANGE_INCREASED("incompatible.response.numeric.range.increased", false),
  RESPONSE_REQUIRED_DECREASED("incompatible.response.required.decreased", true),
  RESPONSE_RESPONSES_DECREASED("incompatible.response.responses.decreased", true),
  RESPONSE_WRITEONLY_INCREASED("incompatible.response.writeonly.increased", true),
  RESPONSE_WRITEONLY_REQUIRED_DECREASED("incompatible.response.writeonly.required.decreased", true),
  SCHEMA_DISCRIMINATOR_CHANGED("incompatible.schema.discriminator.changed", true),
  SCHEMA_TYPE_CHANGED("incompatible.schema.type.changed", true),
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
