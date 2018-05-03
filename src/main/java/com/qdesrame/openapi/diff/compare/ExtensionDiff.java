package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.Changed;
import com.qdesrame.openapi.diff.model.DiffContext;

import java.util.Optional;

public interface ExtensionDiff {

    ExtensionDiff setOpenApiDiff(OpenApiDiff openApiDiff);

    String getName();

    Optional<Changed> diff(Object left, Object right, DiffContext context);
}
