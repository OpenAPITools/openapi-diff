package com.qdesrame.openapi.diff.compare.schemadiffresult;

import com.qdesrame.openapi.diff.compare.SchemaDiff;
import com.qdesrame.openapi.diff.utils.RefPointer;
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
    public SchemaDiffResult diff(Components leftComponents, Components rightComponents, Schema left, Schema right) {
        ArraySchema leftArraySchema = (ArraySchema) left;
        ArraySchema rightArraySchema = (ArraySchema) right;
        left = RefPointer.Replace.schema(leftComponents, leftArraySchema.getItems());
        right = RefPointer.Replace.schema(rightComponents, rightArraySchema.getItems());
        return SchemaDiff.fromComponents(leftComponents, rightComponents).diff(left, right);
    }
}
