package org.openapitools.openapidiff.core.model.schema;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.OPERATION_ID_CHANGED;

import java.util.Objects;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.DiffResult;

public class ChangedOperationId implements Changed {

  private final String left;
  private final String right;
  private final DiffContext context;

  public ChangedOperationId(String left, String right, DiffContext context) {
    this.left = left;
    this.right = right;
    this.context = context;
  }

  @Override
  public DiffResult isChanged() {
    if (Objects.equals(left, right)) {
      return DiffResult.NO_CHANGES;
    }
    if (OPERATION_ID_CHANGED.enabled(context)) {
      return DiffResult.INCOMPATIBLE;
    }
    return DiffResult.METADATA;
  }

  public String getLeft() {
    return this.left;
  }

  public String getRight() {
    return this.right;
  }
}
