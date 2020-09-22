package com.qdesrame.openapi.diff.core.compare;

import com.qdesrame.openapi.diff.core.model.DiffContext;
import java.util.Objects;

public final class CacheKey {
  private final String left;
  private final String right;
  private final DiffContext context;

  public CacheKey(final String left, final String right, final DiffContext context) {
    this.left = left;
    this.right = right;
    this.context = context;
  }

  public String getLeft() {
    return this.left;
  }

  public String getRight() {
    return this.right;
  }

  public DiffContext getContext() {
    return this.context;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CacheKey cacheKey = (CacheKey) o;
    return Objects.equals(left, cacheKey.left)
        && Objects.equals(right, cacheKey.right)
        && Objects.equals(context, cacheKey.context);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right, context);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "CacheKey(left="
        + this.getLeft()
        + ", right="
        + this.getRight()
        + ", context="
        + this.getContext()
        + ")";
  }
}
