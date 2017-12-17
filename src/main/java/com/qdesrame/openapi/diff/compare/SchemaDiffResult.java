package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.Changed;
import io.swagger.oas.models.media.Schema;

import java.util.HashMap;
import java.util.Map;

public class SchemaDiffResult implements Changed {
    private Schema oldSchema;
    private Schema newSchema;

    private String type;
    private Map<String, SchemaDiffResult> changed;
    private Map<String, Schema> increased;
    private Map<String, Schema> missing;
    private boolean deprecated;
    private boolean description;
    private boolean title;
    private ListDiff<String> required;
    private boolean defaultVal;
    private ListDiff enumVal;
    private boolean format;
    private boolean readOnly;
    private boolean writeOnly;

    public SchemaDiffResult() {
        increased = new HashMap<>();
        missing = new HashMap<>();
    }

    @Override
    public boolean isDiff() {
        return false;
    }

    public void setChangeType(String type) {
        this.type = type;
    }

    public Map<String, SchemaDiffResult> getChangedProperties() {
        return changed;
    }

    public Map<String, Schema> getIncreasedProperties() {
        return increased;
    }

    public Map<String, Schema> getMissingProperties() {
        return missing;
    }

    public void setChangeDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public void setChangeDescription(boolean description) {
        this.description = description;
    }

    public void setChangeTitle(boolean title) {
        this.title = title;
    }

    public void setChangeRequired(ListDiff<String> required) {
        this.required = required;
    }

    public void setChangeDefault(boolean changeDefault) {
        this.defaultVal = changeDefault;
    }

    public void setChangeEnum(ListDiff changeEnum) {
        this.enumVal = changeEnum;
    }

    public void setChangeFormat(boolean format) {
        this.format = format;
    }

    public void setChangeReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setChangeWriteOnly(boolean writeOnly) {
        this.writeOnly = writeOnly;
    }

    public Schema getOldSchema() {
        return oldSchema;
    }

    public SchemaDiffResult setOldSchema(Schema oldSchema) {
        this.oldSchema = oldSchema;
        return this;
    }

    public Schema getNewSchema() {
        return newSchema;
    }

    public SchemaDiffResult setNewSchema(Schema newSchema) {
        this.newSchema = newSchema;
        return this;
    }

}
