package org.openapitools.openapidiff.core.model.schema;

import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.DiffResult;

public class ChangedMinItems implements Changed {
  private final Integer oldValue;
  private final Integer newValue;
  private final DiffContext context;

  public ChangedMinItems(Integer oldValue, Integer newValue, DiffContext context) {
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.context = context;
  }

  @Override
  public DiffResult isChanged() {
    if (oldValue == newValue) {
      return DiffResult.NO_CHANGES;
    }
    if (oldValue == null && newValue == null) {
      return DiffResult.NO_CHANGES;
    }
    if (oldValue == null || newValue == null) {
      return DiffResult.COMPATIBLE;
    }
    if (newValue > oldValue) {
      return DiffResult.INCOMPATIBLE;
    }
    return DiffResult.COMPATIBLE;
  }

  public Integer getOldValue() {
    return oldValue;
  }

  public Integer getNewValue() {
    return newValue;
  }

  public DiffContext getContext() {
    return context;
  }
}
