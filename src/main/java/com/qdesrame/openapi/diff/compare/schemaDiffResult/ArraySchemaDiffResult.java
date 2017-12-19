package com.qdesrame.openapi.diff.compare.schemaDiffResult;

import io.swagger.oas.models.Components;
import io.swagger.oas.models.media.ArraySchema;
import io.swagger.oas.models.media.Schema;

/**
 * Created by adarsh.sharma on 18/12/17.
 */
public class ArraySchemaDiffResult extends SchemaDiffResult {
    public ArraySchemaDiffResult() {
        super("array");
    }

    @Override
    public void diff(Components leftComponents, Components rightComponents, Schema left, Schema right) {
        ArraySchema leftArraySchema = (ArraySchema) left;
        ArraySchema rightArraySchema = (ArraySchema) right;
        super.diff(leftComponents, rightComponents, leftArraySchema.getItems(), rightArraySchema.getItems());
    }
}
