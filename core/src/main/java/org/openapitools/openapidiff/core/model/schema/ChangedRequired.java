package org.openapitools.openapidiff.core.model.schema;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_REQUIRED_INCREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_REQUIRED_DECREASED;

import java.util.List;
import org.openapitools.openapidiff.core.model.ChangedList;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.DiffResult;

public class ChangedRequired extends ChangedList<String> {

  public ChangedRequired(List<String> oldValue, List<String> newValue, DiffContext context) {
    super(oldValue, newValue, context);
  }

  @Override
  public DiffResult isItemsChanged() {
    if (context.isRequest() && !getIncreased().isEmpty()) {
      if (REQUEST_REQUIRED_INCREASED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    if (context.isResponse() && !getMissing().isEmpty()) {
      if (RESPONSE_REQUIRED_DECREASED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    return DiffResult.COMPATIBLE;
  }
}
