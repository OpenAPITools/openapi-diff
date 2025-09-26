package org.openapitools.openapidiff.core.model;

import java.util.Objects;

public class ChangedExample implements Changed {

  private Object leftExample;
  private Object rightExample;

  public ChangedExample(Object leftExample, Object rightExample) {
    this.leftExample = leftExample;
    this.rightExample = rightExample;
  }

  public Object getLeftExample() {
    return leftExample;
  }

  public void setLeftExample(Object leftExample) {
    this.leftExample = leftExample;
  }

  public Object getRightExample() {
    return rightExample;
  }

  public void setRightExample(Object rightExample) {
    this.rightExample = rightExample;
  }

  @Override
  public DiffResult isChanged() {
    if (!Objects.deepEquals(leftExample, rightExample)) {
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
    ChangedExample that = (ChangedExample) object;
    return Objects.equals(leftExample, that.leftExample)
        && Objects.equals(rightExample, that.rightExample);
  }

  @Override
  public int hashCode() {
    return Objects.hash(leftExample, rightExample);
  }

  @Override
  public String toString() {
    return "ChangedExample{"
        + "leftExample="
        + leftExample
        + ", rightExample="
        + rightExample
        + '}';
  }
}
