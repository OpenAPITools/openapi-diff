package com.qdesrame.openapi.diff.core.model;

import java.util.Objects;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
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
