package com.qdesrame.openapi.diff.compare;

import static com.qdesrame.openapi.diff.utils.ChangedUtils.isChanged;

import com.qdesrame.openapi.diff.model.ChangedSecurityScheme;
import com.qdesrame.openapi.diff.model.DiffContext;
import com.qdesrame.openapi.diff.model.ListDiff;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Created by adarsh.sharma on 11/01/18. */
public class SecuritySchemeDiff extends ReferenceDiffCache<SecurityScheme, ChangedSecurityScheme> {
  private OpenApiDiff openApiDiff;
  private Components leftComponents;
  private Components rightComponents;

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
    SecurityScheme leftSecurityScheme = leftComponents.getSecuritySchemes().get(leftSchemeRef);
    SecurityScheme rightSecurityScheme = rightComponents.getSecuritySchemes().get(rightSchemeRef);
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
        && leftSecurityScheme.getType() == SecurityScheme.Type.OAUTH2) {
      ListDiff<String> scopesDiff = ListDiff.diff(leftScopes, rightScopes);
      if (!scopesDiff.getIncreased().isEmpty() || !scopesDiff.getMissing().isEmpty()) {
        changedSecurityScheme.setChangedScopes(scopesDiff);
      }
    }

    return isChanged(changedSecurityScheme);
  }

  @Override
  protected Optional<ChangedSecurityScheme> computeDiff(
      HashSet<String> refSet,
      SecurityScheme leftSecurityScheme,
      SecurityScheme rightSecurityScheme,
      DiffContext context) {
    ChangedSecurityScheme changedSecurityScheme =
        new ChangedSecurityScheme(leftSecurityScheme, rightSecurityScheme);

    changedSecurityScheme.setChangedDescription(
        !Objects.equals(leftSecurityScheme.getDescription(), rightSecurityScheme.getDescription()));

    switch (leftSecurityScheme.getType()) {
      case APIKEY:
        changedSecurityScheme.setChangedIn(
            !Objects.equals(leftSecurityScheme.getIn(), rightSecurityScheme.getIn()));
        break;

      case OAUTH2:
        openApiDiff
            .getoAuthFlowsDiff()
            .diff(leftSecurityScheme.getFlows(), rightSecurityScheme.getFlows())
            .ifPresent(changedSecurityScheme::setChangedOAuthFlows);
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
        .ifPresent(changedSecurityScheme::setChangedExtensions);

    return Optional.of(changedSecurityScheme);
  }

  private ChangedSecurityScheme getCopyWithoutScopes(ChangedSecurityScheme original) {
    ChangedSecurityScheme changedSecurityScheme =
        new ChangedSecurityScheme(original.getOldSecurityScheme(), original.getNewSecurityScheme());
    changedSecurityScheme.setChangedType(original.isChangedType());
    changedSecurityScheme.setChangedDescription(original.isChangedDescription());
    changedSecurityScheme.setChangedIn(original.isChangedIn());
    changedSecurityScheme.setChangedScheme(original.isChangedScheme());
    changedSecurityScheme.setChangedBearerFormat(original.isChangedBearerFormat());
    changedSecurityScheme.setChangedOAuthFlows(original.getChangedOAuthFlows());
    changedSecurityScheme.setChangedOpenIdConnectUrl(original.isChangedOpenIdConnectUrl());
    return changedSecurityScheme;
  }
}
