package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangedSecurityRequirement implements ComposedChanged {

  private SecurityRequirement oldSecurityRequirement;
  private SecurityRequirement newSecurityRequirement;

  private SecurityRequirement missing;
  private SecurityRequirement increased;
  private List<ChangedSecurityScheme> changed;

  public ChangedSecurityRequirement(
      SecurityRequirement oldSecurityRequirement, SecurityRequirement newSecurityRequirement) {
    this.oldSecurityRequirement = oldSecurityRequirement;
    this.newSecurityRequirement = newSecurityRequirement;
    this.changed = new ArrayList<>();
  }

  @Override
  public List<Changed> getChangedElements() {
    return new ArrayList<>(changed);
  }

  @Override
  public DiffResult isCoreChanged() {
    if (increased == null && missing == null) {
      return DiffResult.NO_CHANGES;
    }
    if (increased == null) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }

  public void addMissing(String key, List<String> scopes) {
    if (missing == null) {
      missing = new SecurityRequirement();
    }
    missing.put(key, scopes);
  }

  public void addIncreased(String key, List<String> scopes) {
    if (increased == null) {
      increased = new SecurityRequirement();
    }
    increased.put(key, scopes);
  }

  public void addChanged(ChangedSecurityScheme changedSecurityScheme) {
    changed.add(changedSecurityScheme);
  }
}
