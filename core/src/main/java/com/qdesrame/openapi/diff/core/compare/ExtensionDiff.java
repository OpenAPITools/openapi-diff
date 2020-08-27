package com.qdesrame.openapi.diff.core.compare;

import com.qdesrame.openapi.diff.core.model.Change;
import com.qdesrame.openapi.diff.core.model.Changed;
import com.qdesrame.openapi.diff.core.model.DiffContext;

public interface ExtensionDiff {

  ExtensionDiff setOpenApiDiff(OpenApiDiff openApiDiff);

  String getName();

  Changed diff(Change extension, DiffContext context);

  default boolean isParentApplicable(
      Change.Type type, Object object, Object extension, DiffContext context) {
    return true;
  }
}
