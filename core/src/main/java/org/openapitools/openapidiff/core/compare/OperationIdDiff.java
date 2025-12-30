package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;

import java.util.Optional;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.schema.ChangedOperationId;

public class OperationIdDiff {
  private final OpenApiDiff openApiDiff;

  public OperationIdDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  public Optional<ChangedOperationId> diff(String left, String right, DiffContext context) {
    return isChanged(new ChangedOperationId(left, right, context));
  }
}
