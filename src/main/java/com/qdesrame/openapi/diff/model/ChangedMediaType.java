package com.qdesrame.openapi.diff.model;

import io.swagger.oas.models.media.Schema;

public class ChangedMediaType implements Changed {
    private Schema oldSchema;
    private Schema newSchema;
    private ChangedSchema changedSchema;

    public ChangedMediaType(Schema oldSchema, Schema newSchema) {
        this.oldSchema = oldSchema;
        this.newSchema = newSchema;
    }

    @Override
    public boolean isDiff() {
        return changedSchema.isDiff();
    }

    public void setChangedSchema(ChangedSchema changedSchema) {
        this.changedSchema = changedSchema;
    }

    public Schema getOldSchema() {
        return oldSchema;
    }

    public Schema getNewSchema() {
        return newSchema;
    }

    public ChangedSchema getChangedSchema() {
        return changedSchema;
    }
}
