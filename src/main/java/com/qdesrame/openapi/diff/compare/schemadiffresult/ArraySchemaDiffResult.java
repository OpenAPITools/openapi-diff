package com.qdesrame.openapi.diff.compare.schemadiffresult;

import com.qdesrame.openapi.diff.compare.OpenApiDiff;
import com.qdesrame.openapi.diff.model.ChangedSchema;
import io.swagger.oas.models.Components;
import io.swagger.oas.models.media.ArraySchema;
import io.swagger.oas.models.media.Schema;

/**
 * Created by adarsh.sharma on 18/12/17.
 */
public class ArraySchemaDiffResult extends SchemaDiffResult {
    public ArraySchemaDiffResult(OpenApiDiff openApiDiff) {
        super("array", openApiDiff);
    }

    @Override
    public ChangedSchema diff(Components leftComponents, Components rightComponents, Schema left, Schema right) {
        ArraySchema leftArraySchema = (ArraySchema) left;
        ArraySchema rightArraySchema = (ArraySchema) right;
        return openApiDiff.getSchemaDiff().diff(leftArraySchema.getItems(), rightArraySchema.getItems());
    }
}
