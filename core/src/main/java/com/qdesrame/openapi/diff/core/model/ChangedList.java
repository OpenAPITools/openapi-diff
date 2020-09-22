package com.qdesrame.openapi.diff.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class ChangedList<T> implements Changed {
  protected DiffContext context;
  protected List<T> oldValue;
  protected List<T> newValue;
  private List<T> increased;
  private List<T> missing;
  private List<T> shared;

  protected ChangedList(List<T> oldValue, List<T> newValue, DiffContext context) {
    this.oldValue = Optional.ofNullable(oldValue).orElse(new ArrayList<>());
    this.newValue = Optional.ofNullable(newValue).orElse(new ArrayList<>());
    this.context = context;
    this.shared = new ArrayList<>();
    this.increased = new ArrayList<>();
    this.missing = new ArrayList<>();
  }

  @Override
  public DiffResult isChanged() {
    if (missing.isEmpty() && increased.isEmpty()) {
      return DiffResult.NO_CHANGES;
    }
    return isItemsChanged();
  }

  public abstract DiffResult isItemsChanged();

  public static class SimpleChangedList<T> extends ChangedList<T> {
    public SimpleChangedList(List<T> oldValue, List<T> newValue) {
      super(oldValue, newValue, null);
    }

    @Override
    public DiffResult isItemsChanged() {
      return DiffResult.UNKNOWN;
    }
  }

  public DiffContext getContext() {
    return this.context;
  }

  public List<T> getOldValue() {
    return this.oldValue;
  }

  public List<T> getNewValue() {
    return this.newValue;
  }

  public List<T> getIncreased() {
    return this.increased;
  }

  public List<T> getMissing() {
    return this.missing;
  }

  public List<T> getShared() {
    return this.shared;
  }

  public ChangedList<T> setContext(final DiffContext context) {
    this.context = context;
    return this;
  }

  public ChangedList<T> setOldValue(final List<T> oldValue) {
    this.oldValue = oldValue;
    return this;
  }

  public ChangedList<T> setNewValue(final List<T> newValue) {
    this.newValue = newValue;
    return this;
  }

  public ChangedList<T> setIncreased(final List<T> increased) {
    this.increased = increased;
    return this;
  }

  public ChangedList<T> setMissing(final List<T> missing) {
    this.missing = missing;
    return this;
  }

  public ChangedList<T> setShared(final List<T> shared) {
    this.shared = shared;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedList<?> that = (ChangedList<?>) o;
    return Objects.equals(context, that.context)
        && Objects.equals(oldValue, that.oldValue)
        && Objects.equals(newValue, that.newValue)
        && Objects.equals(increased, that.increased)
        && Objects.equals(missing, that.missing)
        && Objects.equals(shared, that.shared);
  }

  @Override
  public int hashCode() {
    return Objects.hash(context, oldValue, newValue, increased, missing, shared);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedList(context="
        + this.getContext()
        + ", oldValue="
        + this.getOldValue()
        + ", newValue="
        + this.getNewValue()
        + ", increased="
        + this.getIncreased()
        + ", missing="
        + this.getMissing()
        + ", shared="
        + this.getShared()
        + ")";
  }
}
