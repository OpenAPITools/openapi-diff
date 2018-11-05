package com.qdesrame.openapi.diff.model;

import java.util.Optional;

public interface Changed {
  static DiffResult result(Changed changed) {
    return Optional.ofNullable(changed).map(Changed::isChanged).orElse(DiffResult.NO_CHANGES);
  }

  DiffResult isChanged();

  default boolean isCompatible() {
    return isChanged().isCompatible();
  }

  default boolean isIncompatible() {
    return isChanged().isIncompatible();
  }

  default boolean isUnchanged() {
    return isChanged().isUnchanged();
  }

  default boolean isDifferent() {
    return isChanged().isDifferent();
  }
}
