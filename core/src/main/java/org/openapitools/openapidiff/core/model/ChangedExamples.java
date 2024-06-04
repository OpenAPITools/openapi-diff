package org.openapitools.openapidiff.core.model;

import java.util.Objects;

public class ChangedExamples implements Changed {

  private Object leftExamples;
  private Object rightExamples;

  public ChangedExamples(Object leftExamples, Object rightExamples) {
    this.leftExamples = leftExamples;
    this.rightExamples = rightExamples;
  }

  public Object getLeftExamples() {
    return leftExamples;
  }

  public void setLeftExamples(Object leftExamples) {
    this.leftExamples = leftExamples;
  }

  public Object getRightExamples() {
    return rightExamples;
  }

  public void setRightExamples(Object rightExamples) {
    this.rightExamples = rightExamples;
  }

  @Override
  public DiffResult isChanged() {
    if (!Objects.equals(leftExamples, rightExamples)) {
      return DiffResult.METADATA;
    }
    return DiffResult.NO_CHANGES;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    ChangedExamples that = (ChangedExamples) object;
    return Objects.equals(leftExamples, that.leftExamples)
        && Objects.equals(rightExamples, that.rightExamples);
  }

  @Override
  public int hashCode() {
    return Objects.hash(leftExamples, rightExamples);
  }

  @Override
  public String toString() {
    return "ChangedExamples{"
        + ", leftExamples="
        + leftExamples
        + ", rightExamples="
        + rightExamples
        + '}';
  }
}
