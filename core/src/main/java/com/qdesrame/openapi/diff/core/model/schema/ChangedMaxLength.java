package com.qdesrame.openapi.diff.core.model.schema;

import com.qdesrame.openapi.diff.core.model.Changed;
import com.qdesrame.openapi.diff.core.model.DiffContext;
import com.qdesrame.openapi.diff.core.model.DiffResult;
import java.util.Objects;
import lombok.Value;

@Value
public class ChangedMaxLength implements Changed {

  Integer oldValue;
  Integer newValue;
  DiffContext context;

  @Override
  public DiffResult isChanged() {
    if (Objects.equals(oldValue, newValue)) {
      return DiffResult.NO_CHANGES;
    }
    if (context.isRequest() && (newValue == null || oldValue != null && oldValue <= newValue)
        || context.isResponse() && (oldValue == null || newValue != null && newValue <= oldValue)) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
