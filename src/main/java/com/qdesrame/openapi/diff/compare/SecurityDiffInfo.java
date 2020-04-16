package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedList;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SecurityDiffInfo {

  private String ref;
  private SecurityScheme securityScheme;
  private List<String> scopes;

  public static SecurityRequirement getSecurityRequirement(
      List<SecurityDiffInfo> securityDiffInfoList) {
    SecurityRequirement securityRequirement = new SecurityRequirement();
    for (SecurityDiffInfo securityDiffInfo : securityDiffInfoList) {
      securityRequirement.put(securityDiffInfo.getRef(), securityDiffInfo.getScopes());
    }

    return securityRequirement;
  }

  public static Optional<List<SecurityDiffInfo>> containsList(
      List<List<SecurityDiffInfo>> securityRequirements, List<SecurityDiffInfo> leftSecurities) {
    return securityRequirements.stream()
        .filter(rightSecurities -> sameList(leftSecurities, rightSecurities))
        .findFirst();
  }

  public static boolean sameList(
      List<SecurityDiffInfo> leftSecurities, List<SecurityDiffInfo> rightSecurities) {
    return ListDiff.diff(new ChangedList.SimpleChangedList<>(leftSecurities, rightSecurities))
        .isUnchanged();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SecurityDiffInfo that = (SecurityDiffInfo) o;

    if (securityScheme != null
        ? !securityScheme.equals(that.securityScheme)
        : that.securityScheme != null) {
      return false;
    }
    return scopes != null ? scopes.equals(that.scopes) : that.scopes == null;
  }

  @Override
  public int hashCode() {
    int result = securityScheme != null ? securityScheme.hashCode() : 0;
    result = 31 * result + (scopes != null ? scopes.hashCode() : 0);
    return result;
  }
}
