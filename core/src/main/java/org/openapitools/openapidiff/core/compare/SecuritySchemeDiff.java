package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.*;
import org.openapitools.openapidiff.core.model.ChangedSecurityScheme;
import org.openapitools.openapidiff.core.model.ChangedSecuritySchemeScopes;
import org.openapitools.openapidiff.core.model.DiffContext;

/** Created by adarsh.sharma on 11/01/18. */
public class SecuritySchemeDiff extends ReferenceDiffCache<SecurityScheme, ChangedSecurityScheme> {
  private final OpenApiDiff openApiDiff;
  private final Components leftComponents;
  private final Components rightComponents;

  public SecuritySchemeDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
    this.leftComponents =
        openApiDiff.getOldSpecOpenApi() != null
            ? openApiDiff.getOldSpecOpenApi().getComponents()
            : null;
    this.rightComponents =
        openApiDiff.getNewSpecOpenApi() != null
            ? openApiDiff.getNewSpecOpenApi().getComponents()
            : null;
  }

  public Optional<ChangedSecurityScheme> diff(
      String leftSchemeRef,
      List<String> leftScopes,
      String rightSchemeRef,
      List<String> rightScopes,
      DiffContext context) {
    SecurityScheme leftSecurityScheme =
        getSecurityScheme(leftComponents.getSecuritySchemes(), leftSchemeRef);
    SecurityScheme rightSecurityScheme =
        getSecurityScheme(rightComponents.getSecuritySchemes(), rightSchemeRef);
    Optional<ChangedSecurityScheme> changedSecuritySchemeOpt =
        cachedDiff(
            new HashSet<>(),
            leftSecurityScheme,
            rightSecurityScheme,
            leftSchemeRef,
            rightSchemeRef,
            context);
    ChangedSecurityScheme changedSecurityScheme =
        changedSecuritySchemeOpt.orElse(
            new ChangedSecurityScheme(leftSecurityScheme, rightSecurityScheme));
    changedSecurityScheme = getCopyWithoutScopes(changedSecurityScheme);

    if (changedSecurityScheme != null
        && leftSecurityScheme != null
        && leftSecurityScheme.getType() == SecurityScheme.Type.OAUTH2) {
      isChanged(ListDiff.diff(new ChangedSecuritySchemeScopes(leftScopes, rightScopes)))
          .ifPresent(changedSecurityScheme::setChangedScopes);
    }

    return isChanged(changedSecurityScheme);
  }

  private SecurityScheme getSecurityScheme(
      Map<String, SecurityScheme> securitySchemes, String leftSchemeRef) {
    return securitySchemes == null ? null : securitySchemes.get(leftSchemeRef);
  }

  @Override
  protected Optional<ChangedSecurityScheme> computeDiff(
      HashSet<String> refSet,
      SecurityScheme leftSecurityScheme,
      SecurityScheme rightSecurityScheme,
      DiffContext context) {
    ChangedSecurityScheme changedSecurityScheme =
        new ChangedSecurityScheme(leftSecurityScheme, rightSecurityScheme);

    if (leftSecurityScheme == null || rightSecurityScheme == null) {
      return Optional.of(changedSecurityScheme);
    }

    openApiDiff
        .getMetadataDiff()
        .diff(leftSecurityScheme.getDescription(), rightSecurityScheme.getDescription(), context)
        .ifPresent(changedSecurityScheme::setDescription);

    switch (leftSecurityScheme.getType()) {
      case APIKEY:
        changedSecurityScheme.setChangedIn(
            !Objects.equals(leftSecurityScheme.getIn(), rightSecurityScheme.getIn()));
        break;

      case OAUTH2:
        openApiDiff
            .getOAuthFlowsDiff()
            .diff(leftSecurityScheme.getFlows(), rightSecurityScheme.getFlows())
            .ifPresent(changedSecurityScheme::setOAuthFlows);
        break;

      case HTTP:
        changedSecurityScheme.setChangedScheme(
            !Objects.equals(leftSecurityScheme.getScheme(), rightSecurityScheme.getScheme()));
        changedSecurityScheme.setChangedBearerFormat(
            !Objects.equals(
                leftSecurityScheme.getBearerFormat(), rightSecurityScheme.getBearerFormat()));
        break;

      case OPENIDCONNECT:
        changedSecurityScheme.setChangedOpenIdConnectUrl(
            !Objects.equals(
                leftSecurityScheme.getOpenIdConnectUrl(),
                rightSecurityScheme.getOpenIdConnectUrl()));
        break;
    }
    openApiDiff
        .getExtensionsDiff()
        .diff(leftSecurityScheme.getExtensions(), rightSecurityScheme.getExtensions(), context)
        .ifPresent(changedSecurityScheme::setExtensions);

    return Optional.of(changedSecurityScheme);
  }

  private ChangedSecurityScheme getCopyWithoutScopes(ChangedSecurityScheme original) {
    return new ChangedSecurityScheme(
            original.getOldSecurityScheme(), original.getNewSecurityScheme())
        .setChangedType(original.isChangedType())
        .setChangedIn(original.isChangedIn())
        .setChangedScheme(original.isChangedScheme())
        .setChangedBearerFormat(original.isChangedBearerFormat())
        .setDescription(original.getDescription())
        .setOAuthFlows(original.getOAuthFlows())
        .setChangedOpenIdConnectUrl(original.isChangedOpenIdConnectUrl());
  }
}
