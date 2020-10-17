package org.openapitools.openapidiff.core.model.schema;

import java.util.Objects;
import java.util.Optional;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.DiffResult;

public class ChangedWriteOnly implements Changed {
  private final DiffContext context;
  private final boolean oldValue;
  private final boolean newValue;
  //    private final boolean required;

  public ChangedWriteOnly(Boolean oldValue, Boolean newValue, DiffContext context) {
    this.context = context;
    this.oldValue = Optional.ofNullable(oldValue).orElse(false);
    this.newValue = Optional.ofNullable(newValue).orElse(false);
    //        this.required = required;
  }

  @Override
  public DiffResult isChanged() {
    if (Objects.equals(oldValue, newValue)) {
      return DiffResult.NO_CHANGES;
    }
    if (context.isRequest()) {
      return DiffResult.COMPATIBLE;
    }
    if (context.isResponse()) {
      if (Boolean.TRUE.equals(newValue)) {
        return DiffResult.INCOMPATIBLE;
      } else {
        return context.isRequired() ? DiffResult.INCOMPATIBLE : DiffResult.COMPATIBLE;
      }
    }
    return DiffResult.UNKNOWN;
  }
}
