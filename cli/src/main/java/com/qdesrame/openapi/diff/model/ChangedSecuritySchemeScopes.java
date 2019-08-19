package com.qdesrame.openapi.diff.model;

import java.util.List;
import lombok.Getter;

@Getter
public class ChangedSecuritySchemeScopes extends ChangedList<String> {

  public ChangedSecuritySchemeScopes(List<String> oldValue, List<String> newValue) {
    super(oldValue, newValue, null);
  }

  @Override
  public DiffResult isItemsChanged() {
    return DiffResult.INCOMPATIBLE;
  }
}
