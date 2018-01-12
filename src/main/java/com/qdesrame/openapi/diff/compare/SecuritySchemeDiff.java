package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedSecurityScheme;
import com.qdesrame.openapi.diff.model.ListDiff;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by adarsh.sharma on 11/01/18.
 */
public class SecuritySchemeDiff {
    private OpenApiDiff openApiDiff;
    private Components leftComponents;
    private Components rightComponents;

    public SecuritySchemeDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.leftComponents = openApiDiff.getOldSpecOpenApi() != null ? openApiDiff.getOldSpecOpenApi().getComponents() : null;
        this.rightComponents = openApiDiff.getNewSpecOpenApi() != null ? openApiDiff.getNewSpecOpenApi().getComponents() : null;
    }

    public Optional<ChangedSecurityScheme> diff(String leftSchemeRef, List<String> leftScopes, String rightSchemeRef, List<String> rightScopes) {
        SecurityScheme leftSecurityScheme = leftComponents.getSecuritySchemes().get(leftSchemeRef);
        SecurityScheme rightSecurityScheme = rightComponents.getSecuritySchemes().get(rightSchemeRef);

        ChangedSecurityScheme changedSecurityScheme = new ChangedSecurityScheme(leftSecurityScheme, rightSecurityScheme);

        changedSecurityScheme.setChangedDescription(!Objects.equals(leftSecurityScheme.getDescription(), rightSecurityScheme.getDescription()));

        switch (leftSecurityScheme.getType()) {
            case APIKEY:
                changedSecurityScheme.setChangedIn(!Objects.equals(leftSecurityScheme.getIn(), rightSecurityScheme.getIn()));
                break;

            case OAUTH2:
                openApiDiff.getoAuthFlowsDiff().diff(leftSecurityScheme.getFlows(), rightSecurityScheme.getFlows())
                        .ifPresent(changedSecurityScheme::setChangedOAuthFlows);
                ListDiff<String> scopesDiff = ListDiff.diff(leftScopes, rightScopes);
                if (!scopesDiff.getIncreased().isEmpty() || !scopesDiff.getMissing().isEmpty()) {
                    changedSecurityScheme.setChangedScopes(scopesDiff);
                }
                break;

            case HTTP:
                changedSecurityScheme.setChangedScheme(!Objects.equals(leftSecurityScheme.getScheme(), rightSecurityScheme.getScheme()));
                changedSecurityScheme.setChangedBearerFormat(!Objects.equals(leftSecurityScheme.getBearerFormat(), rightSecurityScheme.getBearerFormat()));
                break;

            case OPENIDCONNECT:
                changedSecurityScheme.setChangedOpenIdConnectUrl(!Objects.equals(leftSecurityScheme.getOpenIdConnectUrl(), rightSecurityScheme
                        .getOpenIdConnectUrl()));
                break;
        }

        return changedSecurityScheme.isDiff() ? Optional.of(changedSecurityScheme) : Optional.empty();
    }
}
