package com.qdesrame.openapi.diff.core.compare;

import static com.qdesrame.openapi.diff.core.utils.ChangedUtils.isChanged;

import com.qdesrame.openapi.diff.core.model.ChangedSecurityRequirement;
import com.qdesrame.openapi.diff.core.model.ChangedSecurityScheme;
import com.qdesrame.openapi.diff.core.model.DiffContext;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.*;

/** Created by adarsh.sharma on 07/01/18. */
public class SecurityRequirementDiff {
  private OpenApiDiff openApiDiff;
  private Components leftComponents;
  private Components rightComponents;

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
    right.entrySet().stream()
        .forEach(e -> newSecurityRequirement.put(e.getKey(), new ArrayList<>(e.getValue())));
    return newSecurityRequirement;
  }

  private LinkedHashMap<String, List<String>> contains(
      SecurityRequirement right, String schemeRef) {
    SecurityScheme leftSecurityScheme = leftComponents.getSecuritySchemes().get(schemeRef);
    LinkedHashMap<String, List<String>> found = new LinkedHashMap<>();

    for (Map.Entry<String, List<String>> entry : right.entrySet()) {
      SecurityScheme rightSecurityScheme = rightComponents.getSecuritySchemes().get(entry.getKey());
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

  public Optional<ChangedSecurityRequirement> diff(
      SecurityRequirement left, SecurityRequirement right, DiffContext context) {
    ChangedSecurityRequirement changedSecurityRequirement =
        new ChangedSecurityRequirement(left, right != null ? getCopy(right) : null);

    left = left == null ? new SecurityRequirement() : left;
    right = right == null ? new SecurityRequirement() : right;

    for (String leftSchemeRef : left.keySet()) {
      LinkedHashMap<String, List<String>> rightSec = contains(right, leftSchemeRef);
      if (rightSec.isEmpty()) {
        changedSecurityRequirement.addMissing(leftSchemeRef, left.get(leftSchemeRef));
      } else {
        String rightSchemeRef = rightSec.keySet().stream().findFirst().get();
        right.remove(rightSchemeRef);
        Optional<ChangedSecurityScheme> diff =
            openApiDiff
                .getSecuritySchemeDiff()
                .diff(
                    leftSchemeRef,
                    left.get(leftSchemeRef),
                    rightSchemeRef,
                    rightSec.get(rightSchemeRef),
                    context);
        diff.ifPresent(changedSecurityRequirement::addChanged);
      }
    }
    right.forEach(changedSecurityRequirement::addIncreased);

    return isChanged(changedSecurityRequirement);
  }
}
