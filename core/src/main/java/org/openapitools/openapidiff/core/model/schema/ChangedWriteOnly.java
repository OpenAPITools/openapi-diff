package org.openapitools.openapidiff.core.model.schema;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_WRITEONLY_INCREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_WRITEONLY_REQUIRED_DECREASED;

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
        if (RESPONSE_WRITEONLY_INCREASED.enabled(context)) {
          return DiffResult.INCOMPATIBLE;
        }
      } else if (context.isRequired()) {
        if (RESPONSE_WRITEONLY_REQUIRED_DECREASED.enabled(context)) {
          return DiffResult.INCOMPATIBLE;
        }
      }
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.UNKNOWN;
  }
}
