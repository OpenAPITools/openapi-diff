package com.qdesrame.openapi.diff.core.model;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;

@Data
@Accessors(chain = true)
public class ChangedSecurityRequirements implements ComposedChanged {

  private List<SecurityRequirement> oldSecurityRequirements;
  private List<SecurityRequirement> newSecurityRequirements;

  private List<SecurityRequirement> missing;
  private List<SecurityRequirement> increased;
  private List<ChangedSecurityRequirement> changed;

  public ChangedSecurityRequirements(
      List<SecurityRequirement> oldSecurityRequirements,
      List<SecurityRequirement> newSecurityRequirements) {
    this.oldSecurityRequirements = oldSecurityRequirements;
    this.newSecurityRequirements = newSecurityRequirements;
    this.changed = new ArrayList<>();
  }

  @Override
  public List<Changed> getChangedElements() {
    return new ArrayList<>(changed);
  }

  @Override
  public DiffResult isCoreChanged() {
    if (CollectionUtils.isEmpty(missing) && CollectionUtils.isEmpty(increased)) {
      return DiffResult.NO_CHANGES;
    }
    if (CollectionUtils.isEmpty(missing)) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }

  public void addMissing(SecurityRequirement securityRequirement) {
    if (this.missing == null) {
      this.missing = new ArrayList<>();
    }
    this.missing.add(securityRequirement);
  }

  public void addIncreased(SecurityRequirement securityRequirement) {
    if (this.increased == null) {
      this.increased = new ArrayList<>();
    }
    this.increased.add(securityRequirement);
  }

  public void addChanged(ChangedSecurityRequirement changedSecurityRequirement) {
    if (this.changed == null) {
      this.changed = new ArrayList<>();
    }
    this.changed.add(changedSecurityRequirement);
  }
}
