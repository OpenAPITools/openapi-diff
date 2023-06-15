package org.openapitools.openapidiff.core.model.schema;

import java.util.List;
import org.openapitools.openapidiff.core.model.ChangedList;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.DiffResult;

public class ChangedEnum<T> extends ChangedList<T> {

  public ChangedEnum(List<T> oldValue, List<T> newValue, DiffContext context) {
    super(oldValue, newValue, context);
  }

  @Override
  public DiffResult isItemsChanged() {
    if (context.isRequest() && getMissing().isEmpty()) {
      return DiffResult.COMPATIBLE;
    }
    if (context.isResponse()
        && (context.getOptions().isAllowResponseEnumAdditions() || getIncreased().isEmpty())) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
