package com.qdesrame.openapi.diff.compare.schemaDiffResult;

import com.qdesrame.openapi.diff.compare.ListDiff;
import com.qdesrame.openapi.diff.compare.MapKeyDiff;
import com.qdesrame.openapi.diff.compare.SchemaDiff;
import com.qdesrame.openapi.diff.model.Changed;
import com.qdesrame.openapi.diff.utils.RefPointer;
import io.swagger.oas.models.Components;
import io.swagger.oas.models.media.Schema;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SchemaDiffResult implements Changed {
    private Schema oldSchema;
    private Schema newSchema;
    protected String type;
    protected Map<String, SchemaDiffResult> changed;
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

    public SchemaDiffResult() {
        increased = new HashMap<>();
        missing = new HashMap<>();
        changed = new HashMap<>();
    }

    public SchemaDiffResult(String type) {
        this();
        this.type = type;
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
                || required.getIncreased().size() > 0;
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

    public void setChanged(Map<String, SchemaDiffResult> changed) {
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

    public void diff(Components leftComponents, Components rightComponents, Schema left, Schema right) {
        left = RefPointer.Replace.schema(leftComponents, left);
        right = RefPointer.Replace.schema(rightComponents, right);

        this.setOldSchema(left);
        this.setNewSchema(right);
        this.setChangeDeprecated(!Boolean.TRUE.equals(left.getDeprecated()) && Boolean.TRUE.equals(right.getDeprecated()));
        this.setChangeDescription(!Objects.equals(left.getDescription(), right.getDescription()));
        this.setChangeTitle(!Objects.equals(left.getTitle(), right.getTitle()));
        this.setChangeRequired(ListDiff.diff(left.getRequired(), right.getRequired()));
        this.setChangeDefault(!Objects.equals(left.getDefault(), right.getDefault()));
        this.setChangeEnum(ListDiff.diff(left.getEnum(), right.getEnum()));
        this.setChangeFormat(!Objects.equals(left.getFormat(), right.getFormat()));
        this.setChangeReadOnly(!Boolean.TRUE.equals(left.getReadOnly()) && Boolean.TRUE.equals(right.getReadOnly()));
        this.setChangeWriteOnly(!Boolean.TRUE.equals(left.getWriteOnly()) && Boolean.TRUE.equals(right.getWriteOnly()));

        Map<String, Schema> leftProperties = null == left ? null : left.getProperties();
        Map<String, Schema> rightProperties = null == right ? null : right.getProperties();
        MapKeyDiff<String, Schema> propertyDiff = MapKeyDiff.diff(leftProperties, rightProperties);
        Map<String, Schema> increasedProp = propertyDiff.getIncreased();
        Map<String, Schema> missingProp = propertyDiff.getMissing();

        for (String key : propertyDiff.getSharedKey()) {
            Schema leftSchema = RefPointer.Replace.schema(leftComponents, leftProperties.get(key));
            Schema rightSchema = RefPointer.Replace.schema(rightComponents, rightProperties.get(key));

            SchemaDiffResult resultSchema = SchemaDiff.fromComponents(leftComponents, rightComponents).diff(leftSchema, rightSchema);
            if (resultSchema.isDiff()) {
                this.getChangedProperties().put(key, resultSchema);
            }
        }
        this.getIncreasedProperties().putAll(increasedProp);
        this.getMissingProperties().putAll(missingProp);
    }
}
