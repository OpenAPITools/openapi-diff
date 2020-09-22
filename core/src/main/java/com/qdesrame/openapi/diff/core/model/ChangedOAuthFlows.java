package com.qdesrame.openapi.diff.core.model;

import io.swagger.v3.oas.models.security.OAuthFlows;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChangedOAuthFlows implements ComposedChanged {
  private final OAuthFlows oldOAuthFlows;
  private final OAuthFlows newOAuthFlows;
  private ChangedOAuthFlow implicitOAuthFlow;
  private ChangedOAuthFlow passwordOAuthFlow;
  private ChangedOAuthFlow clientCredentialOAuthFlow;
  private ChangedOAuthFlow authorizationCodeOAuthFlow;
  private ChangedExtensions extensions;

  public ChangedOAuthFlows(OAuthFlows oldOAuthFlows, OAuthFlows newOAuthFlows) {
    this.oldOAuthFlows = oldOAuthFlows;
    this.newOAuthFlows = newOAuthFlows;
  }

  @Override
  public List<Changed> getChangedElements() {
    return Arrays.asList(
        implicitOAuthFlow,
        passwordOAuthFlow,
        clientCredentialOAuthFlow,
        authorizationCodeOAuthFlow,
        extensions);
  }

  @Override
  public DiffResult isCoreChanged() {
    return DiffResult.NO_CHANGES;
  }

  public OAuthFlows getOldOAuthFlows() {
    return this.oldOAuthFlows;
  }

  public OAuthFlows getNewOAuthFlows() {
    return this.newOAuthFlows;
  }

  public ChangedOAuthFlow getImplicitOAuthFlow() {
    return this.implicitOAuthFlow;
  }

  public ChangedOAuthFlow getPasswordOAuthFlow() {
    return this.passwordOAuthFlow;
  }

  public ChangedOAuthFlow getClientCredentialOAuthFlow() {
    return this.clientCredentialOAuthFlow;
  }

  public ChangedOAuthFlow getAuthorizationCodeOAuthFlow() {
    return this.authorizationCodeOAuthFlow;
  }

  public ChangedExtensions getExtensions() {
    return this.extensions;
  }

  public ChangedOAuthFlows setImplicitOAuthFlow(final ChangedOAuthFlow implicitOAuthFlow) {
    this.implicitOAuthFlow = implicitOAuthFlow;
    return this;
  }

  public ChangedOAuthFlows setPasswordOAuthFlow(final ChangedOAuthFlow passwordOAuthFlow) {
    this.passwordOAuthFlow = passwordOAuthFlow;
    return this;
  }

  public ChangedOAuthFlows setClientCredentialOAuthFlow(
      final ChangedOAuthFlow clientCredentialOAuthFlow) {
    this.clientCredentialOAuthFlow = clientCredentialOAuthFlow;
    return this;
  }

  public ChangedOAuthFlows setAuthorizationCodeOAuthFlow(
      final ChangedOAuthFlow authorizationCodeOAuthFlow) {
    this.authorizationCodeOAuthFlow = authorizationCodeOAuthFlow;
    return this;
  }

  public ChangedOAuthFlows setExtensions(final ChangedExtensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedOAuthFlows that = (ChangedOAuthFlows) o;
    return Objects.equals(oldOAuthFlows, that.oldOAuthFlows)
        && Objects.equals(newOAuthFlows, that.newOAuthFlows)
        && Objects.equals(implicitOAuthFlow, that.implicitOAuthFlow)
        && Objects.equals(passwordOAuthFlow, that.passwordOAuthFlow)
        && Objects.equals(clientCredentialOAuthFlow, that.clientCredentialOAuthFlow)
        && Objects.equals(authorizationCodeOAuthFlow, that.authorizationCodeOAuthFlow)
        && Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        oldOAuthFlows,
        newOAuthFlows,
        implicitOAuthFlow,
        passwordOAuthFlow,
        clientCredentialOAuthFlow,
        authorizationCodeOAuthFlow,
        extensions);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedOAuthFlows(oldOAuthFlows="
        + this.getOldOAuthFlows()
        + ", newOAuthFlows="
        + this.getNewOAuthFlows()
        + ", implicitOAuthFlow="
        + this.getImplicitOAuthFlow()
        + ", passwordOAuthFlow="
        + this.getPasswordOAuthFlow()
        + ", clientCredentialOAuthFlow="
        + this.getClientCredentialOAuthFlow()
        + ", authorizationCodeOAuthFlow="
        + this.getAuthorizationCodeOAuthFlow()
        + ", extensions="
        + this.getExtensions()
        + ")";
  }
}
