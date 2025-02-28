package org.openapitools.openapidiff.core.model.schema;

import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.DiffResult;

public class ChangedMaxProperties implements Changed {
  private Integer oldValue;
  private Integer newValue;
  private DiffContext context;

  public ChangedMaxProperties(Integer oldValue, Integer newValue, DiffContext context) {
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.context = context;
  }

  public Integer getOldValue() {
    return oldValue;
  }

  public Integer getNewValue() {
    return newValue;
  }

  @Override
  public DiffResult isChanged() {
    if (oldValue == null && newValue == null) {
      return DiffResult.NO_CHANGES;
    }

    if (oldValue == null) {
      return DiffResult.INCOMPATIBLE;
    }

    if (newValue == null) {
      return DiffResult.COMPATIBLE;
    }

    if (!oldValue.equals(newValue)) {
      return oldValue > newValue ? DiffResult.INCOMPATIBLE : DiffResult.COMPATIBLE;
    }

    return DiffResult.NO_CHANGES;
  }
}
