package org.openapitools.openapidiff.core.model;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

import org.apache.commons.configuration2.Configuration;

// Properties to enable/disable backward incompatibility checks
// Note: order is not programmatically significant but keep alphabetical for maintainability
public enum BackwardIncompatibleProp {
  EXTENSION_CONTENT_TYPES_DECREASED("incompatible.extension.content.types.decreased", false),
  EXTENSION_CONTENT_TYPE_DELETED("incompatible.extension.content.type.%s.deleted", false),
  OPENAPI_ENDPOINTS_DECREASED("incompatible.openapi.endpoints.decreased", true),
  OPERATION_ID_CHANGED("incompatible.operation.id.changed", false),
  REQUEST_BODY_REQUIRED_INCREASED("incompatible.request.body.increased", true),
  REQUEST_CONTENT_DECREASED("incompatible.request.content.decreased", true),
  REQUEST_ENUM_DECREASED("incompatible.request.enum.decreased", true),
  REQUEST_MAX_LENGTH_DECREASED("incompatible.request.max.length.decreased", true),
  REQUEST_MIN_LENGTH_INCREASED("incompatible.request.min.length.increased", false),
  REQUEST_NUMERIC_RANGE_DECREASED("incompatible.request.numeric.range.decreased", true),
  REQUEST_ONEOF_DECREASED("incompatible.request.oneof.decreased", true),
  REQUEST_PARAM_ALLOWEMPTY_DECREASED("incompatible.request.param.allowempty.decreased", true),
  REQUEST_PARAM_EXPLODE_CHANGED("incompatible.request.param.explode.changed", true),
  REQUEST_PARAM_STYLE_CHANGED("incompatible.request.param.style.changed", true),
  REQUEST_PARAMS_DECREASED("incompatible.request.params.decreased", true),
  REQUEST_PARAMS_REQUIRED_INCREASED("incompatible.request.params.required.increased", true),
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
  RESPONSE_MIN_LENGTH_INCREASED("incompatible.response.min.length.increased", false),
  RESPONSE_NUMERIC_RANGE_INCREASED("incompatible.response.numeric.range.increased", false),
  RESPONSE_ONEOF_INCREASED("incompatible.response.oneof.increased", true),
  RESPONSE_REQUIRED_DECREASED("incompatible.response.required.decreased", true),
  RESPONSE_RESPONSES_DECREASED("incompatible.response.responses.decreased", true),
  RESPONSE_WRITEONLY_INCREASED("incompatible.response.writeonly.increased", true),
  RESPONSE_WRITEONLY_REQUIRED_DECREASED("incompatible.response.writeonly.required.decreased", true),
  SECURITY_REQUIREMENT_SCHEMES_INCREASED(
      "incompatible.security.requirement.schemes.increased", true),
  SECURITY_REQUIREMENTS_DECREASED("incompatible.security.requirements.decreased", true),
  SECURITY_SCHEME_BEARER_FORMAT_CHANGED("incompatible.security.scheme.bearer.format.changed", true),
  SECURITY_SCHEME_OAUTH2_AUTH_URL_CHANGED(
      "incompatible.security.scheme.oauth2.auth.url.changed", true),
  SECURITY_SCHEME_OAUTH2_REFRESH_URL_CHANGED(
      "incompatible.security.scheme.oauth2.refresh.url.changed", true),
  SECURITY_SCHEME_OAUTH2_TOKEN_URL_CHANGED(
      "incompatible.security.scheme.oauth2.token.url.changed", true),
  SECURITY_SCHEME_OPENIDCONNECT_URL_CHANGED(
      "incompatible.security.scheme.openidconnect.url.changed", true),
  SECURITY_SCHEME_SCHEME_CHANGED("incompatible.security.scheme.scheme.changed", true),
  SECURITY_SCHEME_SCOPES_INCREASED("incompatible.security.scheme.scopes.increased", true),
  SCHEMA_DISCRIMINATOR_CHANGED("incompatible.schema.discriminator.changed", true),
  SCHEMA_PATTERN_CHANGED("incompatible.schema.pattern.changed", true),
  SCHEMA_MIN_PROPERTIES_CHANGED("incompatible.schema.min-properties.changed", true),
  SCHEMA_MAX_PROPERTIES_CHANGED("incompatible.schema.max-properties.changed", true),
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
