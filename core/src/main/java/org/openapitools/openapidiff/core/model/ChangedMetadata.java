package org.openapitools.openapidiff.core.model;

import java.util.Objects;

public class ChangedMetadata implements Changed {
  private String left;
  private String right;

  @Override
  public DiffResult isChanged() {
    if (Objects.equals(left, right)) {
      return DiffResult.NO_CHANGES;
    }
    return DiffResult.METADATA;
  }

  public String getLeft() {
    return this.left;
  }

  public String getRight() {
    return this.right;
  }

  public ChangedMetadata setLeft(final String left) {
    this.left = left;
    return this;
  }

  public ChangedMetadata setRight(final String right) {
    this.right = right;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedMetadata that = (ChangedMetadata) o;
    return Objects.equals(left, that.left) && Objects.equals(right, that.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedMetadata(left=" + this.getLeft() + ", right=" + this.getRight() + ")";
  }
}
