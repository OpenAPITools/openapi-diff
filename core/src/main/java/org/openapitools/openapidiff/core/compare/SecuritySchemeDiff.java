package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.openapitools.openapidiff.core.model.ChangedSecurityScheme;
import org.openapitools.openapidiff.core.model.ChangedSecuritySchemeScopes;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;
import org.openapitools.openapidiff.core.model.deferred.RealizedChanged;

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

  public DeferredChanged<ChangedSecurityScheme> diff(
      String leftSchemeRef,
      List<String> leftScopes,
      String rightSchemeRef,
      List<String> rightScopes,
      DiffContext context) {
    SecurityScheme leftSecurityScheme = leftComponents.getSecuritySchemes().get(leftSchemeRef);
    SecurityScheme rightSecurityScheme = rightComponents.getSecuritySchemes().get(rightSchemeRef);
    DeferredChanged<ChangedSecurityScheme> changedSecuritySchemeOpt =
        cachedDiff(
            new HashSet<>(),
            leftSecurityScheme,
            rightSecurityScheme,
            leftSchemeRef,
            rightSchemeRef,
            context);

    return changedSecuritySchemeOpt.map(
        (changedSecuritySchemeOptional) -> {
          ChangedSecurityScheme changedSecurityScheme =
              changedSecuritySchemeOptional.orElse(
                  new ChangedSecurityScheme(leftSecurityScheme, rightSecurityScheme));
          changedSecurityScheme = getCopyWithoutScopes(changedSecurityScheme);

          if (changedSecurityScheme != null
              && leftSecurityScheme.getType() == SecurityScheme.Type.OAUTH2) {
            isChanged(ListDiff.diff(new ChangedSecuritySchemeScopes(leftScopes, rightScopes)))
                .ifPresent(changedSecurityScheme::setChangedScopes);
          }

          return changedSecurityScheme;
        });
  }

  @Override
  protected DeferredChanged<ChangedSecurityScheme> computeDiff(
      HashSet<String> refSet,
      SecurityScheme leftSecurityScheme,
      SecurityScheme rightSecurityScheme,
      DiffContext context) {
    ChangedSecurityScheme changedSecurityScheme =
        new ChangedSecurityScheme(leftSecurityScheme, rightSecurityScheme);

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

    return new RealizedChanged<ChangedSecurityScheme>(changedSecurityScheme);
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
