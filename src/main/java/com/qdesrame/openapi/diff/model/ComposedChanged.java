package com.qdesrame.openapi.diff.model;

import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotNull;

public interface ComposedChanged extends Changed {

  @NotNull
  List<Changed> getChangedElements();

  @NotNull
  DiffResult isCoreChanged();

  @Override
  default DiffResult isChanged() {
    DiffResult elementsResult =
        DiffResult.fromWeight(
            getChangedElements()
                .stream()
                .filter(Objects::nonNull)
                .map(Changed::isChanged)
                .mapToInt(diffResult -> diffResult.getWeight())
                .max()
                .orElseGet(() -> 0));
    if (isCoreChanged().getWeight() > elementsResult.getWeight()) {
      return isCoreChanged();
    } else {
      return elementsResult;
    }
  }
}
