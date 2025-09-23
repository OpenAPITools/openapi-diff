package org.openapitools.openapidiff.core.model;

import java.lang.reflect.Array;
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
    DiffResult arrayDiff = compareArrays();
    if (arrayDiff != null) {
      return arrayDiff;
    }

    return Objects.equals(leftExample, rightExample) ? DiffResult.NO_CHANGES : DiffResult.METADATA;
  }

  private DiffResult compareArrays() {
    if (leftExample == null || rightExample == null) {
      return null;
    }

    if (!leftExample.getClass().isArray() || !rightExample.getClass().isArray()) {
      return null;
    }

    int leftLength = Array.getLength(leftExample);

    if (leftLength != Array.getLength(rightExample)) {
      return DiffResult.METADATA;
    }

    for (int i = 0; i < leftLength; i++) {
      if (!Objects.deepEquals(Array.get(leftExample, i), Array.get(rightExample, i))) {
        return DiffResult.METADATA;
      }
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
