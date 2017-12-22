package com.qdesrame.openapi.diff.model;

import io.swagger.oas.models.media.Schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adarsh.sharma on 22/12/17.
 */
public class ChangedSchema implements Changed {
    protected Schema oldSchema;
    protected Schema newSchema;
    protected String type;
    protected Map<String, ChangedSchema> changed;
    protected Map<String, Schema> increased;
    protected Map<String, Schema> missing;
    protected boolean deprecated;
    protected boolean description;
    protected boolean title;
    protected ListDiff<String> required;
    protected boolean defaultVal;
    protected ListDiff enumVal;
    protected boolean format;
    protected boolean readOnly;
    protected boolean writeOnly;
    protected boolean changedType;
    protected boolean discriminatorPropertyChanged;
    protected ChangedOneOfSchema changedOneOfSchema;

    public ChangedSchema() {
        increased = new HashMap<>();
        missing = new HashMap<>();
        changed = new HashMap<>();
    }

    @Override
    public boolean isDiff() {
        return Boolean.TRUE.equals(changedType)
                || writeOnly
                || readOnly
                || format
                || increased.size() > 0
                || missing.size() > 0
                || changed.size() > 0
                || deprecated
                || required.getIncreased().size() > 0
                || required.getMissing().size() > 0
                || discriminatorPropertyChanged
                || (changedOneOfSchema != null && changedOneOfSchema.isDiff());
    }

    public void setChangeType(String type) {
        this.type = type;
    }

    public Map<String, ChangedSchema> getChangedProperties() {
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

    public void setChanged(Map<String, ChangedSchema> changed) {
        this.changed = changed;
    }

    public void setIncreased(Map<String, Schema> increased) {
        this.increased = increased;
    }

    public void addIncreased(String name, Schema increased) {
        this.increased.put(name, increased);
    }

    public void setMissing(Map<String, Schema> missing) {
        this.missing = missing;
    }

    public void addMissing(String name, Schema missing) {
        this.missing.put(name, missing);
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public void setDescription(boolean description) {
        this.description = description;
    }

    public void setTitle(boolean title) {
        this.title = title;
    }

    public void setRequired(ListDiff<String> required) {
        this.required = required;
    }

    public void setDefaultVal(boolean defaultVal) {
        this.defaultVal = defaultVal;
    }

    public void setEnumVal(ListDiff enumVal) {
        this.enumVal = enumVal;
    }

    public void setFormat(boolean format) {
        this.format = format;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setWriteOnly(boolean writeOnly) {
        this.writeOnly = writeOnly;
    }

    public void setChangedType(boolean changedType) {
        this.changedType = changedType;
    }

    public Schema getOldSchema() {
        return oldSchema;
    }

    public ChangedSchema setOldSchema(Schema oldSchema) {
        this.oldSchema = oldSchema;
        return this;
    }

    public Schema getNewSchema() {
        return newSchema;
    }

    public ChangedSchema setNewSchema(Schema newSchema) {
        this.newSchema = newSchema;
        return this;
    }

    public void setDiscriminatorPropertyChanged(boolean discriminatorPropertyChanged) {
        this.discriminatorPropertyChanged = discriminatorPropertyChanged;
    }

    public void setChangedOneOfSchema(ChangedOneOfSchema changedOneOfSchema) {
        this.changedOneOfSchema = changedOneOfSchema;
    }
}
