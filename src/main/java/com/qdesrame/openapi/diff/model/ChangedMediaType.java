package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangedMediaType implements RequestResponseChanged {
    private Schema oldSchema;
    private Schema newSchema;
    private ChangedSchema changedSchema;

    public ChangedMediaType(Schema oldSchema, Schema newSchema) {
        this.oldSchema = oldSchema;
        this.newSchema = newSchema;
    }

    @Override
    public boolean isDiff() {
        return (changedSchema != null && changedSchema.isDiff());
    }

    @Override
    public boolean isDiffBackwardCompatible(boolean isRequest) {
        return (changedSchema == null || changedSchema.isDiffBackwardCompatible(isRequest));
    }

}
