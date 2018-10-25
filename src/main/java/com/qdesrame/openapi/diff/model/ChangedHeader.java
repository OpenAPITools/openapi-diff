package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.model.schema.ChangedExtensions;
import com.qdesrame.openapi.diff.utils.ChangedUtils;
import io.swagger.v3.oas.models.headers.Header;
import lombok.Getter;
import lombok.Setter;

/** Created by adarsh.sharma on 28/12/17. */
@Getter
@Setter
public class ChangedHeader implements Changed {
  private final Header oldHeader;
  private final Header newHeader;
  private final DiffContext context;

  private boolean changeDescription;
  private boolean changeRequired;
  private boolean changeDeprecated;
  private boolean changeStyle;
  private boolean changeExplode;
  private ChangedSchema changedSchema;
  private ChangedContent changedContent;
  private ChangedExtensions changedExtensions;

  public ChangedHeader(Header oldHeader, Header newHeader, DiffContext context) {
    this.oldHeader = oldHeader;
    this.newHeader = newHeader;
    this.context = context;
  }

  @Override
  public DiffResult isChanged() {
    if (!changeDescription
        && !changeRequired
        && !changeDeprecated
        && !changeStyle
        && !changeExplode
        && ChangedUtils.isUnchanged(changedSchema)
        && ChangedUtils.isUnchanged(changedContent)
        && ChangedUtils.isUnchanged(changedExtensions)) {
      return DiffResult.NO_CHANGES;
    }
    if (!changeRequired
        && !changeStyle
        && !changeExplode
        && ChangedUtils.isCompatible(changedSchema)
        && ChangedUtils.isCompatible(changedContent)
        && ChangedUtils.isCompatible(changedExtensions)) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
