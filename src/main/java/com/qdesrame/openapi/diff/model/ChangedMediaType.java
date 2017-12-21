package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.compare.schemadiffresult.SchemaDiffResult;

public class ChangedMediaType implements Changed {
    private SchemaDiffResult schema;


    @Override
    public boolean isDiff() {
        return schema.isDiff();
    }

    public void setSchema(SchemaDiffResult schema) {
        this.schema = schema;
    }

    public SchemaDiffResult getSchema() {
        return schema;
    }
}
