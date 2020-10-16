package com.qdesrame.openapi.diff.core.model;

import java.util.Objects;

public final class Change<T> {
  private final T oldValue;
  private final T newValue;
  private final Type type;

  public static <T> Change<T> changed(T oldValue, T newValue) {
    return new Change<>(oldValue, newValue, Type.CHANGED);
  }

  public static <T> Change<T> added(T newValue) {
    return new Change<>(null, newValue, Type.ADDED);
  }

  public static <T> Change<T> removed(T oldValue) {
    return new Change<>(oldValue, null, Type.REMOVED);
  }

  public enum Type {
    ADDED,
    CHANGED,
    REMOVED;
  }

  public Change(final T oldValue, final T newValue, final Type type) {
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.type = type;
  }

  public T getOldValue() {
    return this.oldValue;
  }

  public T getNewValue() {
    return this.newValue;
  }

  public Type getType() {
    return this.type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Change<?> change = (Change<?>) o;
    return Objects.equals(oldValue, change.oldValue)
        && Objects.equals(newValue, change.newValue)
        && type == change.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(oldValue, newValue, type);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "Change(oldValue="
        + this.getOldValue()
        + ", newValue="
        + this.getNewValue()
        + ", type="
        + this.getType()
        + ")";
  }
}
