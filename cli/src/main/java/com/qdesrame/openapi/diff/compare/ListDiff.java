package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedList;
import lombok.Getter;

@Getter
public class ListDiff {

  public static <K extends ChangedList<X>, X> K diff(K instance) {
    if (instance.getOldValue() == null && instance.getNewValue() == null) {
      return instance;
    }
    if (instance.getOldValue() == null) {
      instance.setIncreased(instance.getNewValue());
      return instance;
    }
    if (instance.getNewValue() == null) {
      instance.setMissing(instance.getOldValue());
      return instance;
    }
    instance.getIncreased().addAll(instance.getNewValue());
    for (X leftItem : instance.getOldValue()) {
      if (instance.getNewValue().contains(leftItem)) {
        instance.getIncreased().remove(leftItem);
        instance.getShared().add(leftItem);
      } else {
        instance.getMissing().add(leftItem);
      }
    }
    return instance;
  }
}
