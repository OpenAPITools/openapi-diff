package com.qdesrame.openapi.diff.compare.schemadiffresult;

import com.qdesrame.openapi.diff.compare.OpenApiDiff;
import com.qdesrame.openapi.diff.model.ChangedSchema;
import com.qdesrame.openapi.diff.model.DiffContext;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.HashSet;
import java.util.Optional;

/**
 * Created by adarsh.sharma on 18/12/17.
 */
public class ArraySchemaDiffResult extends SchemaDiffResult {
    public ArraySchemaDiffResult(OpenApiDiff openApiDiff) {
        super("array", openApiDiff);
    }

    @Override
    public Optional<ChangedSchema> diff(HashSet<String> refSet, Components leftComponents, Components rightComponents, Schema left, Schema right, DiffContext context) {
        ArraySchema leftArraySchema = (ArraySchema) left;
        ArraySchema rightArraySchema = (ArraySchema) right;
        return openApiDiff.getSchemaDiff().diff(refSet, leftArraySchema.getItems(), rightArraySchema.getItems(), context);
    }
}
