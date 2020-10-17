package org.openapitools.openapidiff.core.model;

import java.util.List;

public class ChangedSecuritySchemeScopes extends ChangedList<String> {

  public ChangedSecuritySchemeScopes(List<String> oldValue, List<String> newValue) {
    super(oldValue, newValue, null);
  }

  @Override
  public DiffResult isItemsChanged() {
    return DiffResult.INCOMPATIBLE;
  }
}
