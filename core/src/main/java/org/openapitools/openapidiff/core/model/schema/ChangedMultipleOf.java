package org.openapitools.openapidiff.core.model.schema;

import java.math.BigDecimal;
import java.util.Objects;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.DiffResult;

public class ChangedMultipleOf implements Changed {

  private final BigDecimal left;
  private final BigDecimal right;

  public ChangedMultipleOf(BigDecimal leftMultipleOf, BigDecimal rightMultipleOf) {
    this.left = leftMultipleOf;
    this.right = rightMultipleOf;
  }

  @Override
  public DiffResult isChanged() {
    if (Objects.equals(left, right)) {
      return DiffResult.NO_CHANGES;
    }

    // multipleof removed -> compatible
    if (right == null) {
      return DiffResult.COMPATIBLE;
    }

    return DiffResult.INCOMPATIBLE;
  }

  public BigDecimal getLeft() {
    return left;
  }

  public BigDecimal getRight() {
    return right;
  }

  @Override
  public String toString() {
    return "ChangedMultipleOf [left=" + left + ", right=" + right + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedMultipleOf that = (ChangedMultipleOf) o;
    return Objects.equals(left, that.getLeft()) && Objects.equals(right, that.getRight());
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }
}
