package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.Change;
import com.qdesrame.openapi.diff.model.Changed;
import com.qdesrame.openapi.diff.model.DiffContext;

public interface ExtensionDiff {

    ExtensionDiff setOpenApiDiff(OpenApiDiff openApiDiff);

    String getName();

    Changed diff(Change extension, DiffContext context);

    default boolean isParentApplicable(Change.Type type, Object object, Object extension, DiffContext context) {
        return true;
    }

}
