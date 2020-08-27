package com.qdesrame.openapi.diff.core.model.schema;

import com.qdesrame.openapi.diff.core.model.ChangedList;
import com.qdesrame.openapi.diff.core.model.DiffContext;
import com.qdesrame.openapi.diff.core.model.DiffResult;
import java.util.List;

public class ChangedEnum<T> extends ChangedList<T> {

  public ChangedEnum(List<T> oldValue, List<T> newValue, DiffContext context) {
    super(oldValue, newValue, context);
  }

  @Override
  public DiffResult isItemsChanged() {
    if (context.isRequest() && getMissing().isEmpty()
        || context.isResponse() && getIncreased().isEmpty()) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
