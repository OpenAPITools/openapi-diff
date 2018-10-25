package com.qdesrame.openapi.diff.model;

public class CompatibleChanged implements Changed {
  private final Object value;

  private CompatibleChanged(Object value) {
    this.value = value;
  }

  public static CompatibleChanged compatible(Change change) {
    return new CompatibleChanged(change);
  }

  @Override
  public DiffResult isChanged() {
    return DiffResult.COMPATIBLE;
  }

  public Object getValue() {
    return value;
  }
}
