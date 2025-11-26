package org.openapitools.openapidiff.core.model.schema;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_MIN_LENGTH_INCREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_MIN_LENGTH_INCREASED;

import java.util.Objects;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.DiffResult;

public final class ChangedMinLength implements Changed {
  private final Integer oldValue;
  private final Integer newValue;
  private final DiffContext context;

  @Override
  public DiffResult isChanged() {
    if (Objects.equals(oldValue, newValue)) {
      return DiffResult.NO_CHANGES;
    }
    if (context.isRequest() && (newValue != null && (oldValue == null || newValue > oldValue))) {
      if (REQUEST_MIN_LENGTH_INCREASED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    if (context.isResponse() && (newValue != null && (oldValue == null || newValue > oldValue))) {
      if (RESPONSE_MIN_LENGTH_INCREASED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    return DiffResult.COMPATIBLE;
  }

  public ChangedMinLength(
      final Integer oldValue, final Integer newValue, final DiffContext context) {
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.context = context;
  }

  public Integer getOldValue() {
    return this.oldValue;
  }

  public Integer getNewValue() {
    return this.newValue;
  }

  public DiffContext getContext() {
    return this.context;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedMinLength that = (ChangedMinLength) o;
    return Objects.equals(oldValue, that.oldValue)
        && Objects.equals(newValue, that.newValue)
        && Objects.equals(context, that.context);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oldValue, newValue, context);
  }

  @Override
  public String toString() {
    return "ChangedMinLength(oldValue="
        + this.getOldValue()
        + ", newValue="
        + this.getNewValue()
        + ", context="
        + this.getContext()
        + ")";
  }
}
