package com.qdesrame.openapi.diff.model.schema;

import com.qdesrame.openapi.diff.model.ChangedList;
import com.qdesrame.openapi.diff.model.DiffContext;
import com.qdesrame.openapi.diff.model.DiffResult;
import java.util.List;

public class ChangedRequired extends ChangedList<String> {

  public ChangedRequired(List<String> oldValue, List<String> newValue, DiffContext context) {
    super(oldValue, newValue, context);
  }

  @Override
  public DiffResult isItemsChanged() {
    if (context.isRequest() && getIncreased().isEmpty()
        || context.isResponse() && getMissing().isEmpty()) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
