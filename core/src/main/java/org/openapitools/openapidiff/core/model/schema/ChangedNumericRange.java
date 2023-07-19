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

    boolean exclusiveMaxOld = oldMaximumExclusiveValue != null && oldMaximumExclusiveValue;
    boolean exclusiveMinOld = oldMinimumExclusiveValue != null && oldMinimumExclusiveValue;
    boolean exclusiveMaxNew = newMaximumExclusiveValue != null && newMaximumExclusiveValue;
    boolean exclusiveMinNew = newMinimumExclusiveValue != null && newMinimumExclusiveValue;
    int diffMax = compare(oldMaximumValue, newMaximumValue, false);
    int diffMin = compare(oldMinimumValue, newMinimumValue, true);

    if (context.isRequest()) {
      if (diffMax > 0 || (diffMax == 0 && !exclusiveMaxOld && exclusiveMaxNew)) {
        return DiffResult.INCOMPATIBLE;
      }
      if (diffMin < 0 || (diffMin == 0 && !exclusiveMinOld && exclusiveMinNew)) {
        return DiffResult.INCOMPATIBLE;
      }
    } else if (context.isResponse()) {
      if (diffMax < 0 || (diffMax == 0 && exclusiveMaxOld && !exclusiveMaxNew)) {
        return DiffResult.INCOMPATIBLE;
      }
      if (diffMin > 0 || (diffMin == 0 && exclusiveMinOld && !exclusiveMinNew)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    return DiffResult.COMPATIBLE;
  }

  private int compare(BigDecimal left, BigDecimal right, boolean nullMeansLessThan) {
    if (left == null && right == null) {
      return 0;
    }
    if (left == null) {
      return nullMeansLessThan ? -1 : 1;
    }
    if (right == null) {
      return nullMeansLessThan ? 1 : -1;
    }
    return left.unscaledValue().compareTo(right.unscaledValue());
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
