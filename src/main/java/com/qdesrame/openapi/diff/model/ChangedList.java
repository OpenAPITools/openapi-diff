package com.qdesrame.openapi.diff.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
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
}
