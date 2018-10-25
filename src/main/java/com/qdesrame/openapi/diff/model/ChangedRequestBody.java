package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.model.schema.ChangedExtensions;
import com.qdesrame.openapi.diff.utils.ChangedUtils;
import io.swagger.v3.oas.models.parameters.RequestBody;
import lombok.Getter;
import lombok.Setter;

/** Created by adarsh.sharma on 27/12/17. */
@Getter
@Setter
public class ChangedRequestBody implements Changed {
  private final RequestBody oldRequestBody;
  private final RequestBody newRequestBody;
  private final DiffContext context;

  private boolean changeDescription;
  private boolean changeRequired;
  private ChangedContent changedContent;
  private ChangedExtensions changedExtensions;

  public ChangedRequestBody(
      RequestBody oldRequestBody, RequestBody newRequestBody, DiffContext context) {
    this.oldRequestBody = oldRequestBody;
    this.newRequestBody = newRequestBody;
    this.context = context;
  }

  @Override
  public DiffResult isChanged() {
    if (!changeDescription
        && !changeRequired
        && ChangedUtils.isUnchanged(changedContent)
        && ChangedUtils.isUnchanged(changedExtensions)) {
      return DiffResult.NO_CHANGES;
    }
    if (!changeRequired
        && ChangedUtils.isCompatible(changedContent)
        && ChangedUtils.isCompatible(changedExtensions)) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
