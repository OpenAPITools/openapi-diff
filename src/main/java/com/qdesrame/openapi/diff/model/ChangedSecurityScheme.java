package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/** Created by adarsh.sharma on 11/01/18. */
@Getter
@Setter
@Accessors(chain = true)
public class ChangedSecurityScheme implements ComposedChanged {
  private SecurityScheme oldSecurityScheme;
  private SecurityScheme newSecurityScheme;

  private boolean changedType;
  private boolean changedIn;
  private boolean changedScheme;
  private boolean changedBearerFormat;
  private boolean changedOpenIdConnectUrl;
  private ListDiff<String> changedScopes;
  private ChangedMetadata description;
  private ChangedOAuthFlows oAuthFlows;
  private ChangedExtensions extensions;

  public ChangedSecurityScheme(SecurityScheme oldSecurityScheme, SecurityScheme newSecurityScheme) {
    this.oldSecurityScheme = oldSecurityScheme;
    this.newSecurityScheme = newSecurityScheme;
  }

  @Override
  public List<Changed> getChangedElements() {
    return Arrays.asList(description, oAuthFlows, extensions);
  }

  @Override
  public DiffResult isCoreChanged() {
    if (!changedType
        && !changedIn
        && !changedScheme
        && !changedBearerFormat
        && !changedOpenIdConnectUrl
        && (changedScopes == null || changedScopes.isUnchanged())) {
      return DiffResult.NO_CHANGES;
    }
    if (!changedType
        && !changedIn
        && !changedScheme
        && !changedBearerFormat
        && !changedOpenIdConnectUrl
        && (changedScopes == null || changedScopes.getIncreased().isEmpty())) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
