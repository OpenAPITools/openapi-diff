package com.qdesrame.openapi.diff.compare.schemadiffresult;

import com.qdesrame.openapi.diff.compare.MapKeyDiff;
import com.qdesrame.openapi.diff.compare.OpenApiDiff;
import com.qdesrame.openapi.diff.model.ChangedSchema;
import com.qdesrame.openapi.diff.model.ListDiff;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Map;
import java.util.Objects;

public class SchemaDiffResult {
    protected ChangedSchema changedSchema;
    protected OpenApiDiff openApiDiff;

    public SchemaDiffResult(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.changedSchema = new ChangedSchema();
    }

    public SchemaDiffResult(String type, OpenApiDiff openApiDiff) {
        this(openApiDiff);
        this.changedSchema.setChangeType(type);
    }

    public ChangedSchema getChangedSchema() {
        return changedSchema;
    }

    public ChangedSchema diff(Components leftComponents, Components rightComponents, Schema left, Schema right) {
        changedSchema.setOldSchema(left);
        changedSchema.setNewSchema(right);
        changedSchema.setChangeDeprecated(!Boolean.TRUE.equals(left.getDeprecated()) && Boolean.TRUE.equals(right.getDeprecated()));
        changedSchema.setChangeDescription(!Objects.equals(left.getDescription(), right.getDescription()));
        changedSchema.setChangeTitle(!Objects.equals(left.getTitle(), right.getTitle()));
        changedSchema.setChangeRequired(ListDiff.diff(left.getRequired(), right.getRequired()));
        changedSchema.setChangeDefault(!Objects.equals(left.getDefault(), right.getDefault()));
        changedSchema.setChangeEnum(ListDiff.diff(left.getEnum(), right.getEnum()));
        changedSchema.setChangeFormat(!Objects.equals(left.getFormat(), right.getFormat()));
        changedSchema.setChangeReadOnly(!Boolean.TRUE.equals(left.getReadOnly()) && Boolean.TRUE.equals(right.getReadOnly()));
        changedSchema.setChangeWriteOnly(!Boolean.TRUE.equals(left.getWriteOnly()) && Boolean.TRUE.equals(right.getWriteOnly()));

        Map<String, Schema> leftProperties = null == left ? null : left.getProperties();
        Map<String, Schema> rightProperties = null == right ? null : right.getProperties();
        MapKeyDiff<String, Schema> propertyDiff = MapKeyDiff.diff(leftProperties, rightProperties);
        Map<String, Schema> increasedProp = propertyDiff.getIncreased();
        Map<String, Schema> missingProp = propertyDiff.getMissing();

        for (String key : propertyDiff.getSharedKey()) {
            ChangedSchema resultSchema = openApiDiff.getSchemaDiff().diff(leftProperties.get(key), rightProperties.get(key));
            if (resultSchema.isDiff()) {
                changedSchema.getChangedProperties().put(key, resultSchema);
            }
        }
        changedSchema.getIncreasedProperties().putAll(increasedProp);
        changedSchema.getMissingProperties().putAll(missingProp);
        return changedSchema;
    }

}
