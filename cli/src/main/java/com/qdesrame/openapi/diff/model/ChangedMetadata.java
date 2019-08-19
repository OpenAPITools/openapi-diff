package com.qdesrame.openapi.diff.model;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ChangedMetadata implements Changed {

  private String left;
  private String right;

  @Override
  public DiffResult isChanged() {
    if (Objects.equals(left, right)) {
      return DiffResult.NO_CHANGES;
    }
    return DiffResult.METADATA;
  }
}
