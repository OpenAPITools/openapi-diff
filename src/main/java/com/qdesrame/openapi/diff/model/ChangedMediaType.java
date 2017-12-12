package com.qdesrame.openapi.diff.model;

import java.util.List;

public class ChangedMediaType implements Changed {
    private List<ElSchema> addProps;
    private List<ElSchema> missingProps;

    public void setAddProps(List<ElSchema> addProps) {
        this.addProps = addProps;
    }

    public void setMissingProps(List<ElSchema> missingProps) {
        this.missingProps = missingProps;
    }


    @Override
    public boolean isDiff() {
        return !addProps.isEmpty() || !missingProps.isEmpty();
    }
}
