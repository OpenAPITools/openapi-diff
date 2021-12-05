package org.openapitools.openapidiff.core.compare;

import org.openapitools.openapidiff.core.model.Change;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.DiffContext;

public interface ExtensionDiff {

  ExtensionDiff setOpenApiDiff(OpenApiDiff openApiDiff);

  String getName();

  Changed diff(Change<?> extension, DiffContext context);

  default boolean isParentApplicable(
      Change.Type type, Object object, Object extension, DiffContext context) {
    return true;
  }
}
