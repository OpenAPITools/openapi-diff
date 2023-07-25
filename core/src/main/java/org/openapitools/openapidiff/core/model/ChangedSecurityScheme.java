package org.openapitools.openapidiff.core.model;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.*;

import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChangedSecurityScheme implements ComposedChanged {
  private SecurityScheme oldSecurityScheme;
  private SecurityScheme newSecurityScheme;
  private final DiffContext context;
  private boolean changedType;
  private boolean changedIn;
  private boolean changedScheme;
  private boolean changedBearerFormat;
  private boolean changedOpenIdConnectUrl;
  private ChangedSecuritySchemeScopes changedScopes;
  private ChangedMetadata description;
  private ChangedOAuthFlows oAuthFlows;
  private ChangedExtensions extensions;

  public ChangedSecurityScheme(
      SecurityScheme oldSecurityScheme, SecurityScheme newSecurityScheme, DiffContext context) {
    this.oldSecurityScheme = oldSecurityScheme;
    this.newSecurityScheme = newSecurityScheme;
    this.context = context;
  }

  @Override
  public List<Changed> getChangedElements() {
    return Arrays.asList(description, oAuthFlows, extensions);
  }

  @Override
  public DiffResult isCoreChanged() {
    if (!changedType
        && !changedIn
        && !changedScheme
        && !changedBearerFormat
        && !changedOpenIdConnectUrl
        && (changedScopes == null || changedScopes.isUnchanged())) {
      return DiffResult.NO_CHANGES;
    }

    if (changedBearerFormat) {
      if (SECURITY_SCHEME_BEARER_FORMAT_CHANGED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    if (changedOpenIdConnectUrl) {
      if (SECURITY_SCHEME_OPENIDCONNECT_URL_CHANGED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    if (changedScheme) {
      if (SECURITY_SCHEME_SCHEME_CHANGED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    if (changedScopes != null && !changedScopes.getIncreased().isEmpty()) {
      if (SECURITY_SCHEME_SCOPES_INCREASED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    if (changedIn || changedType) {
      // TODO: Dead code removal opportunity for changedType and changedIn. It appears that
      // SecuritySchemaDiff will never be given the chance to detect differences TYPE and
      // IN differences because that case has already been detected and filtered out by
      // SecurityRequirementsDiff and recorded as a dropped requirement in
      // ChangedSecurityRequirements.
      return DiffResult.INCOMPATIBLE;
    }
    return DiffResult.COMPATIBLE;
  }

  public SecurityScheme getOldSecurityScheme() {
    return this.oldSecurityScheme;
  }

  public SecurityScheme getNewSecurityScheme() {
    return this.newSecurityScheme;
  }

  public boolean isChangedType() {
    return this.changedType;
  }

  public boolean isChangedIn() {
    return this.changedIn;
  }

  public boolean isChangedScheme() {
    return this.changedScheme;
  }

  public boolean isChangedBearerFormat() {
    return this.changedBearerFormat;
  }

  public boolean isChangedOpenIdConnectUrl() {
    return this.changedOpenIdConnectUrl;
  }

  public ChangedSecuritySchemeScopes getChangedScopes() {
    return this.changedScopes;
  }

  public ChangedMetadata getDescription() {
    return this.description;
  }

  public ChangedOAuthFlows getOAuthFlows() {
    return this.oAuthFlows;
  }

  public ChangedExtensions getExtensions() {
    return this.extensions;
  }

  public ChangedSecurityScheme setOldSecurityScheme(final SecurityScheme oldSecurityScheme) {
    this.oldSecurityScheme = oldSecurityScheme;
    return this;
  }

  public ChangedSecurityScheme setNewSecurityScheme(final SecurityScheme newSecurityScheme) {
    this.newSecurityScheme = newSecurityScheme;
    return this;
  }

  public ChangedSecurityScheme setChangedType(final boolean changedType) {
    this.changedType = changedType;
    return this;
  }

  public ChangedSecurityScheme setChangedIn(final boolean changedIn) {
    this.changedIn = changedIn;
    return this;
  }

  public ChangedSecurityScheme setChangedScheme(final boolean changedScheme) {
    this.changedScheme = changedScheme;
    return this;
  }

  public ChangedSecurityScheme setChangedBearerFormat(final boolean changedBearerFormat) {
    this.changedBearerFormat = changedBearerFormat;
    return this;
  }

  public ChangedSecurityScheme setChangedOpenIdConnectUrl(final boolean changedOpenIdConnectUrl) {
    this.changedOpenIdConnectUrl = changedOpenIdConnectUrl;
    return this;
  }

  public ChangedSecurityScheme setChangedScopes(final ChangedSecuritySchemeScopes changedScopes) {
    this.changedScopes = changedScopes;
    return this;
  }

  public ChangedSecurityScheme setDescription(final ChangedMetadata description) {
    this.description = description;
    return this;
  }

  public ChangedSecurityScheme setOAuthFlows(final ChangedOAuthFlows oAuthFlows) {
    this.oAuthFlows = oAuthFlows;
    return this;
  }

  public ChangedSecurityScheme setExtensions(final ChangedExtensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedSecurityScheme that = (ChangedSecurityScheme) o;
    return changedType == that.changedType
        && changedIn == that.changedIn
        && changedScheme == that.changedScheme
        && changedBearerFormat == that.changedBearerFormat
        && changedOpenIdConnectUrl == that.changedOpenIdConnectUrl
        && Objects.equals(oldSecurityScheme, that.oldSecurityScheme)
        && Objects.equals(newSecurityScheme, that.newSecurityScheme)
        && Objects.equals(changedScopes, that.changedScopes)
        && Objects.equals(description, that.description)
        && Objects.equals(oAuthFlows, that.oAuthFlows)
        && Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        oldSecurityScheme,
        newSecurityScheme,
        changedType,
        changedIn,
        changedScheme,
        changedBearerFormat,
        changedOpenIdConnectUrl,
        changedScopes,
        description,
        oAuthFlows,
        extensions);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedSecurityScheme(oldSecurityScheme="
        + this.getOldSecurityScheme()
        + ", newSecurityScheme="
        + this.getNewSecurityScheme()
        + ", changedType="
        + this.isChangedType()
        + ", changedIn="
        + this.isChangedIn()
        + ", changedScheme="
        + this.isChangedScheme()
        + ", changedBearerFormat="
        + this.isChangedBearerFormat()
        + ", changedOpenIdConnectUrl="
        + this.isChangedOpenIdConnectUrl()
        + ", changedScopes="
        + this.getChangedScopes()
        + ", description="
        + this.getDescription()
        + ", oAuthFlows="
        + this.getOAuthFlows()
        + ", extensions="
        + this.getExtensions()
        + ")";
  }
}
