package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangedMediaType implements Changed {
    private final Schema oldSchema;
    private final Schema newSchema;
    private final DiffContext context;
    private ChangedSchema changedSchema;

    public ChangedMediaType(Schema oldSchema, Schema newSchema, DiffContext context) {
        this.oldSchema = oldSchema;
        this.newSchema = newSchema;
        this.context = context;
    }

    @Override
    public DiffResult isChanged() {
        if (this.changedSchema == null || this.changedSchema.isUnchanged()) {
            return DiffResult.NO_CHANGES;
        }
        if (this.changedSchema.isCompatible()) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }

}
