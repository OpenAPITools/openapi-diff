package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

/** Created by adarsh.sharma on 06/01/18. */
@Getter
@Setter
public class ChangedSecurityRequirements implements Changed {
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
  }

  @Override
  public DiffResult isChanged() {
    if (CollectionUtils.isEmpty(missing)
        && CollectionUtils.isEmpty(increased)
        && CollectionUtils.isEmpty(changed)) {
      return DiffResult.NO_CHANGES;
    }
    if (CollectionUtils.isEmpty(missing)
        && (changed == null || changed.stream().allMatch(Changed::isCompatible))) {
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
