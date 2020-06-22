package com.qdesrame.openapi.diff.core.model;

import lombok.Value;

@Value
public class Change<T> {

  T oldValue;
  T newValue;
  Type type;

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
