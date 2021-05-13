package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.*;
import org.openapitools.openapidiff.core.model.ChangedSecurityRequirement;
import org.openapitools.openapidiff.core.model.DiffContext;

/** Created by adarsh.sharma on 07/01/18. */
public class SecurityRequirementDiff {
  private final OpenApiDiff openApiDiff;
  private final Components leftComponents;
  private final Components rightComponents;

  public SecurityRequirementDiff(OpenApiDiff openApiDiff) {
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

  public static SecurityRequirement getCopy(LinkedHashMap<String, List<String>> right) {
    SecurityRequirement newSecurityRequirement = new SecurityRequirement();
    right.forEach((key, value) -> newSecurityRequirement.put(key, new ArrayList<>(value)));
    return newSecurityRequirement;
  }

  private LinkedHashMap<String, List<String>> contains(
      SecurityRequirement right, String schemeRef) {
    SecurityScheme leftSecurityScheme = getSecurityScheme(schemeRef, leftComponents);

    LinkedHashMap<String, List<String>> found = new LinkedHashMap<>();

    for (Map.Entry<String, List<String>> entry : right.entrySet()) {

      SecurityScheme rightSecurityScheme = getSecurityScheme(entry.getKey(), rightComponents);

      if (leftSecurityScheme == null || rightSecurityScheme == null) {
        found.put(entry.getKey(), entry.getValue());
        return found;
      }

      if (leftSecurityScheme.getType() == rightSecurityScheme.getType()) {
        switch (leftSecurityScheme.getType()) {
          case APIKEY:
            if (leftSecurityScheme.getName().equals(rightSecurityScheme.getName())) {
              found.put(entry.getKey(), entry.getValue());
              return found;
            }
            break;

          case OAUTH2:
          case HTTP:
          case OPENIDCONNECT:
            found.put(entry.getKey(), entry.getValue());
            return found;
        }
      }
    }

    return found;
  }

  private SecurityScheme getSecurityScheme(String schemeRef, Components components) {
    final Map<String, SecurityScheme> securitySchemeMap = components.getSecuritySchemes();
    return securitySchemeMap == null ? null : securitySchemeMap.get(schemeRef);
  }

  public Optional<ChangedSecurityRequirement> diff(
      SecurityRequirement left, SecurityRequirement right, DiffContext context) {
    ChangedSecurityRequirement changedSecurityRequirement =
        new ChangedSecurityRequirement(left, right != null ? getCopy(right) : null);

    SecurityRequirement leftRequirement = left == null ? new SecurityRequirement() : left;
    SecurityRequirement rightRequirement = right == null ? new SecurityRequirement() : right;

    for (Map.Entry<String, List<String>> leftEntry : leftRequirement.entrySet()) {
      LinkedHashMap<String, List<String>> rightSec = contains(rightRequirement, leftEntry.getKey());
      if (rightSec.isEmpty()) {
        changedSecurityRequirement.addMissing(leftEntry.getKey(), leftEntry.getValue());
      } else {
        Optional<String> rightSchemeRef = rightSec.keySet().stream().findFirst();
        rightSchemeRef.ifPresent(rightRequirement::remove);
        rightSchemeRef
            .flatMap(
                rightScheme ->
                    openApiDiff
                        .getSecuritySchemeDiff()
                        .diff(
                            leftEntry.getKey(),
                            leftEntry.getValue(),
                            rightScheme,
                            rightSec.get(rightScheme),
                            context))
            .ifPresent(changedSecurityRequirement::addChanged);
      }
    }
    rightRequirement.forEach(changedSecurityRequirement::addIncreased);

    return isChanged(changedSecurityRequirement);
  }
}
