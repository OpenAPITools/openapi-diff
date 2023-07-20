package org.openapitools.openapidiff.core.model.schema;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_READONLY_INCREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_READONLY_REQUIRED_DECREASED;

import java.util.Objects;
import java.util.Optional;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.DiffResult;

public class ChangedReadOnly implements Changed {
  private final DiffContext context;
  private final boolean oldValue;
  private final boolean newValue;
  //    private final boolean required;

  public ChangedReadOnly(Boolean oldValue, Boolean newValue, DiffContext context) {
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
    if (context.isResponse()) {
      return DiffResult.COMPATIBLE;
    }
    if (context.isRequest()) {
      if (Boolean.TRUE.equals(newValue)) {
        if (REQUEST_READONLY_INCREASED.enabled(context)) {
          return DiffResult.INCOMPATIBLE;
        }
      } else if (context.isRequired()) {
        // Incompatible because a prev RO prop (invalid) is now not RO and required
        if (REQUEST_READONLY_REQUIRED_DECREASED.enabled(context)) {
          return DiffResult.INCOMPATIBLE;
        }
      }
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.UNKNOWN;
  }
}
