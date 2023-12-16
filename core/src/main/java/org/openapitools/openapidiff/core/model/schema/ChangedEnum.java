package org.openapitools.openapidiff.core.model.schema;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_ENUM_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_ENUM_INCREASED;

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
    if (context.isRequest() && !getMissing().isEmpty()) {
      if (REQUEST_ENUM_DECREASED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    if (context.isResponse() && !getIncreased().isEmpty()) {
      if (RESPONSE_ENUM_INCREASED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    return DiffResult.COMPATIBLE;
  }
}
