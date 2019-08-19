package com.qdesrame.openapi.diff.model;

import lombok.Getter;

@Getter
public class Change<T> {
  private final T oldValue;
  private final T newValue;
  private final Type type;

  private Change(T oldValue, T newValue, Type type) {
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.type = type;
  }

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
    REMOVED
  }
}
