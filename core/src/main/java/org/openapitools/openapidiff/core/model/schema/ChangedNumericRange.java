package org.openapitools.openapidiff.core.model.schema;

import java.math.BigDecimal;
import java.util.Objects;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.DiffResult;

public final class ChangedNumericRange implements Changed {
  private final BigDecimal oldMinimumValue;
  private final BigDecimal newMinimumValue;
  private final BigDecimal oldMaximumValue;
  private final BigDecimal newMaximumValue;
  private final Boolean oldMinimumExclusiveValue;
  private final Boolean newMinimumExclusiveValue;
  private final Boolean oldMaximumExclusiveValue;
  private final Boolean newMaximumExclusiveValue;
  private final DiffContext context;

  @Override
  public DiffResult isChanged() {
    if (Objects.equals(oldMinimumValue, newMinimumValue)
        && Objects.equals(oldMaximumValue, newMaximumValue)
        && Objects.equals(oldMinimumExclusiveValue, newMinimumExclusiveValue)
        && Objects.equals(oldMaximumExclusiveValue, newMaximumExclusiveValue)) {
      return DiffResult.NO_CHANGES;
    }
    if (context.isRequest()
            && (newMinimumValue == null
                || oldMinimumValue != null
                    && oldMinimumValue.unscaledValue().compareTo(newMinimumValue.unscaledValue())
                        >= 0)
            && (newMaximumValue == null
                || oldMaximumValue != null
                    && oldMaximumValue.unscaledValue().compareTo(newMaximumValue.unscaledValue())
                        <= 0)
            && (newMinimumExclusiveValue == null
                || oldMinimumExclusiveValue != null && newMinimumExclusiveValue == true)
            && (newMaximumExclusiveValue == null
                || oldMaximumExclusiveValue != null && newMaximumExclusiveValue == true)
        || context.isResponse()
            && (newMinimumValue == null
                || oldMinimumValue != null
                    && oldMinimumValue.unscaledValue().compareTo(newMinimumValue.unscaledValue())
                        >= 0)
            && (newMaximumValue == null
                || oldMaximumValue != null
                    && oldMaximumValue.unscaledValue().compareTo(newMaximumValue.unscaledValue())
                        <= 0)
            && (newMinimumExclusiveValue == null
                || oldMinimumExclusiveValue != null && newMinimumExclusiveValue == true)
            && (newMaximumExclusiveValue == null
                || oldMaximumExclusiveValue != null && newMaximumExclusiveValue == true)) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }

  public ChangedNumericRange(
      final BigDecimal oldMinimumValue,
      final BigDecimal newMinimumValue,
      final BigDecimal oldMaximumValue,
      final BigDecimal newMaximumValue,
      final Boolean oldMinimumExclusiveValue,
      final Boolean newMinimumExclusiveValue,
      final Boolean oldMaximumExclusiveValue,
      final Boolean newMaximumExclusiveValue,
      final DiffContext context) {
    this.oldMinimumValue = oldMinimumValue;
    this.newMinimumValue = newMinimumValue;
    this.oldMaximumValue = oldMaximumValue;
    this.newMaximumValue = newMaximumValue;
    this.oldMinimumExclusiveValue = oldMinimumExclusiveValue;
    this.newMinimumExclusiveValue = newMinimumExclusiveValue;
    this.oldMaximumExclusiveValue = oldMaximumExclusiveValue;
    this.newMaximumExclusiveValue = newMaximumExclusiveValue;
    this.context = context;
  }

  public BigDecimal getOldMinimumValue() {
    return oldMinimumValue;
  }

  public BigDecimal getNewMinimumValue() {
    return newMinimumValue;
  }

  public BigDecimal getOldMaximumValue() {
    return oldMaximumValue;
  }

  public BigDecimal getNewMaximumValue() {
    return newMaximumValue;
  }

  public Boolean getOldMinimumExclusiveValue() {
    return oldMinimumExclusiveValue;
  }

  public Boolean getNewMinimumExclusiveValue() {
    return newMinimumExclusiveValue;
  }

  public Boolean getOldMaximumExclusiveValue() {
    return oldMaximumExclusiveValue;
  }

  public Boolean getNewMaximumExclusiveValue() {
    return newMaximumExclusiveValue;
  }

  public DiffContext getContext() {
    return this.context;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedNumericRange that = (ChangedNumericRange) o;
    return Objects.equals(oldMinimumValue, newMinimumValue)
        && Objects.equals(oldMaximumValue, newMaximumValue)
        && Objects.equals(oldMinimumExclusiveValue, newMinimumExclusiveValue)
        && Objects.equals(oldMaximumExclusiveValue, newMaximumExclusiveValue)
        && Objects.equals(context, that.context);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        oldMinimumValue,
        newMinimumValue,
        oldMaximumValue,
        newMaximumValue,
        oldMinimumExclusiveValue,
        newMinimumExclusiveValue,
        oldMaximumExclusiveValue,
        newMaximumExclusiveValue,
        context);
  }

  @Override
  public String toString() {
    return "ChangedNumericRange("
        + "oldMinimumValue="
        + oldMinimumValue
        + ", newMinimumValue="
        + newMinimumValue
        + ", oldMaximumValue="
        + oldMaximumValue
        + ", newMaximumValue="
        + newMaximumValue
        + ", oldMinimumExclusiveValue="
        + oldMinimumExclusiveValue
        + ", newMinimumExclusiveValue="
        + newMinimumExclusiveValue
        + ", oldMaximumExclusiveValue="
        + oldMaximumExclusiveValue
        + ", newMaximumExclusiveValue="
        + newMaximumExclusiveValue
        + ", context="
        + context
        + ')';
  }
}
