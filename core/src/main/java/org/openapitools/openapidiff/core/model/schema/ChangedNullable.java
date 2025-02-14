package org.openapitools.openapidiff.core.model.schema;

import java.util.Objects;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.DiffResult;

public class ChangedNullable implements Changed {

  private final Boolean left;
  private final Boolean right;

  public ChangedNullable(Boolean leftNullable, Boolean rightNullable) {
    this.left = leftNullable;
    this.right = rightNullable;
  }

  @Override
  public DiffResult isChanged() {
    boolean leftValue = left != null && left;
    boolean rightValue = right != null && right;

    if (leftValue != rightValue) {
      return DiffResult.COMPATIBLE;
    }

    return DiffResult.NO_CHANGES;
  }

  public Boolean getLeft() {
    return left;
  }

  public Boolean getRight() {
    return right;
  }

  @Override
  public String toString() {
    return "ChangedNullable [left=" + left + ", right=" + right + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedNullable that = (ChangedNullable) o;
    return Objects.equals(left, that.getLeft()) && Objects.equals(right, that.getRight());
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }
}
