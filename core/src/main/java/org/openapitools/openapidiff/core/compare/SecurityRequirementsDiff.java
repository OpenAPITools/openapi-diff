package org.openapitools.openapidiff.core.compare;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedSecurityRequirements;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredBuilder;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;

public class SecurityRequirementsDiff {
  private final OpenApiDiff openApiDiff;
  private final Components leftComponents;
  private final Components rightComponents;

  public SecurityRequirementsDiff(OpenApiDiff openApiDiff) {
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

  public Optional<SecurityRequirement> contains(
      List<SecurityRequirement> securityRequirements, SecurityRequirement left) {
    return securityRequirements.stream()
        .filter(rightSecurities -> same(left, rightSecurities))
        .findFirst();
  }

  public boolean same(SecurityRequirement left, SecurityRequirement right) {
    List<Pair<SecurityScheme.Type, SecurityScheme.In>> leftTypes =
        getListOfSecuritySchemes(leftComponents, left);
    List<Pair<SecurityScheme.Type, SecurityScheme.In>> rightTypes =
        getListOfSecuritySchemes(rightComponents, right);

    return CollectionUtils.isEqualCollection(leftTypes, rightTypes);
  }

  private List<Pair<SecurityScheme.Type, SecurityScheme.In>> getListOfSecuritySchemes(
      Components components, SecurityRequirement securityRequirement) {
    return securityRequirement.keySet().stream()
        .map(
            x -> {
              if (components == null) {
                throw new IllegalArgumentException("Missing securitySchemes component definition.");
              }
              Map<String, SecurityScheme> securitySchemes = components.getSecuritySchemes();
              if (securitySchemes == null) {
                throw new IllegalArgumentException("Missing securitySchemes component definition.");
              }

              SecurityScheme result = securitySchemes.get(x);
              if (result == null) {
                throw new IllegalArgumentException("Impossible to find security scheme: " + x);
              }

              return result;
            })
        .map(this::getPair)
        .distinct()
        .collect(Collectors.toList());
  }

  private Pair<SecurityScheme.Type, SecurityScheme.In> getPair(SecurityScheme securityScheme) {
    return new ImmutablePair<>(securityScheme.getType(), securityScheme.getIn());
  }

  protected DeferredChanged<ChangedSecurityRequirements> diff(
      List<SecurityRequirement> left, List<SecurityRequirement> right, DiffContext context) {

    DeferredBuilder<Changed> builder = new DeferredBuilder<>();

    left = left == null ? new ArrayList<>() : left;
    right = right == null ? new ArrayList<>() : getCopy(right);

    ChangedSecurityRequirements changedSecurityRequirements =
        new ChangedSecurityRequirements(left, right);

    for (SecurityRequirement leftSecurity : left) {
      Optional<SecurityRequirement> rightSecOpt = contains(right, leftSecurity);
      if (!rightSecOpt.isPresent()) {
        changedSecurityRequirements.addMissing(leftSecurity);
      } else {
        SecurityRequirement rightSec = rightSecOpt.get();
        right.remove(rightSec);
        builder
            .with(openApiDiff.getSecurityRequirementDiff().diff(leftSecurity, rightSec, context))
            .ifPresent(changedSecurityRequirements::addChanged);
      }
    }
    right.forEach(changedSecurityRequirements::addIncreased);

    return builder.buildIsChanged(changedSecurityRequirements);
  }

  private List<SecurityRequirement> getCopy(List<SecurityRequirement> right) {
    return right.stream().map(SecurityRequirementDiff::getCopy).collect(Collectors.toList());
  }
}
