package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.utils.RefPointer;
import io.swagger.oas.models.Components;
import io.swagger.oas.models.media.Schema;

import java.util.Map;
import java.util.Objects;

public class SchemaDiff {

    private Components leftComponents;
    private Components rightComponents;

    private SchemaDiff(Components left, Components right) {
        this.leftComponents = left;
        this.rightComponents = right;
    }

    public static SchemaDiff fromComponents(Components left, Components right) {
        return new SchemaDiff(left, right);
    }

    public SchemaDiffResult diff(Schema left, Schema right) {
        SchemaDiffResult result = new SchemaDiffResult();
        RefPointer.Replace.schema(leftComponents, left);
        RefPointer.Replace.schema(rightComponents, right);

        if (!Objects.equals(left.getType(), right.getType())) {
            result.setChangeType(right.getType());
        }
        result.setOldSchema(left);
        result.setNewSchema(right);
        result.setChangeDeprecated(!Boolean.TRUE.equals(left.getDeprecated()) && Boolean.TRUE.equals(right.getDeprecated()));
        result.setChangeDescription(!Objects.equals(left.getDescription(), right.getDescription()));
        result.setChangeTitle(!Objects.equals(left.getTitle(), right.getTitle()));
        result.setChangeRequired(ListDiff.diff(left.getRequired(), right.getRequired()));
        result.setChangeDefault(!Objects.equals(left.getDefault(), right.getDefault()));
        result.setChangeEnum(ListDiff.diff(left.getEnum(), right.getEnum()));
        result.setChangeFormat(!Objects.equals(left.getFormat(), right.getFormat()));
        result.setChangeReadOnly(!Boolean.TRUE.equals(left.getReadOnly()) && Boolean.TRUE.equals(right.getReadOnly()));
        result.setChangeWriteOnly(!Boolean.TRUE.equals(left.getWriteOnly()) && Boolean.TRUE.equals(right.getWriteOnly()));

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
                result.getChangedProperties().put(key, resultSchema);
            }
        }
        result.getIncreasedProperties().putAll(increasedProp);
        result.getMissingProperties().putAll(missingProp);
        return result;
    }

}
