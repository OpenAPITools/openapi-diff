package org.openapitools.openapidiff.core.model;

import io.swagger.v3.oas.models.security.OAuthFlow;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ChangedOAuthFlow implements ComposedChanged {
  private OAuthFlow oldOAuthFlow;
  private OAuthFlow newOAuthFlow;
  private boolean authorizationUrl;
  private boolean tokenUrl;
  private boolean refreshUrl;
  private ChangedExtensions extensions;

  public ChangedOAuthFlow(OAuthFlow oldOAuthFlow, OAuthFlow newOAuthFlow) {
    this.oldOAuthFlow = oldOAuthFlow;
    this.newOAuthFlow = newOAuthFlow;
  }

  @Override
  public List<Changed> getChangedElements() {
    return Collections.singletonList(extensions);
  }

  @Override
  public DiffResult isCoreChanged() {
    if (authorizationUrl || tokenUrl || refreshUrl) {
      return DiffResult.INCOMPATIBLE;
    }
    return DiffResult.NO_CHANGES;
  }

  public OAuthFlow getOldOAuthFlow() {
    return this.oldOAuthFlow;
  }

  public OAuthFlow getNewOAuthFlow() {
    return this.newOAuthFlow;
  }

  public boolean isAuthorizationUrl() {
    return this.authorizationUrl;
  }

  public boolean isTokenUrl() {
    return this.tokenUrl;
  }

  public boolean isRefreshUrl() {
    return this.refreshUrl;
  }

  public ChangedExtensions getExtensions() {
    return this.extensions;
  }

  public ChangedOAuthFlow setOldOAuthFlow(final OAuthFlow oldOAuthFlow) {
    this.oldOAuthFlow = oldOAuthFlow;
    return this;
  }

  public ChangedOAuthFlow setNewOAuthFlow(final OAuthFlow newOAuthFlow) {
    this.newOAuthFlow = newOAuthFlow;
    return this;
  }

  public ChangedOAuthFlow setAuthorizationUrl(final boolean authorizationUrl) {
    this.authorizationUrl = authorizationUrl;
    return this;
  }

  public ChangedOAuthFlow setTokenUrl(final boolean tokenUrl) {
    this.tokenUrl = tokenUrl;
    return this;
  }

  public ChangedOAuthFlow setRefreshUrl(final boolean refreshUrl) {
    this.refreshUrl = refreshUrl;
    return this;
  }

  public ChangedOAuthFlow setExtensions(final ChangedExtensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedOAuthFlow that = (ChangedOAuthFlow) o;
    return authorizationUrl == that.authorizationUrl
        && tokenUrl == that.tokenUrl
        && refreshUrl == that.refreshUrl
        && Objects.equals(oldOAuthFlow, that.oldOAuthFlow)
        && Objects.equals(newOAuthFlow, that.newOAuthFlow)
        && Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        oldOAuthFlow, newOAuthFlow, authorizationUrl, tokenUrl, refreshUrl, extensions);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedOAuthFlow(oldOAuthFlow="
        + this.getOldOAuthFlow()
        + ", newOAuthFlow="
        + this.getNewOAuthFlow()
        + ", authorizationUrl="
        + this.isAuthorizationUrl()
        + ", tokenUrl="
        + this.isTokenUrl()
        + ", refreshUrl="
        + this.isRefreshUrl()
        + ", extensions="
        + this.getExtensions()
        + ")";
  }
}
