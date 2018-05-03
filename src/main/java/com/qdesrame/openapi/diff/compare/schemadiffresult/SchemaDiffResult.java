package com.qdesrame.openapi.diff.compare.schemadiffresult;

import com.qdesrame.openapi.diff.compare.MapKeyDiff;
import com.qdesrame.openapi.diff.compare.OpenApiDiff;
import com.qdesrame.openapi.diff.model.ChangedSchema;
import com.qdesrame.openapi.diff.model.DiffContext;
import com.qdesrame.openapi.diff.model.ListDiff;
import com.qdesrame.openapi.diff.model.schema.ChangedExtensions;
import com.qdesrame.openapi.diff.model.schema.ChangedReadOnly;
import com.qdesrame.openapi.diff.model.schema.ChangedWriteOnly;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

import static com.qdesrame.openapi.diff.utils.ChangedUtils.isChanged;

@Getter
public class SchemaDiffResult {
    protected ChangedSchema changedSchema;
    protected OpenApiDiff openApiDiff;

    public SchemaDiffResult(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.changedSchema = new ChangedSchema();
    }

    public SchemaDiffResult(String type, OpenApiDiff openApiDiff) {
        this(openApiDiff);
        this.changedSchema.setType(type);
    }

    public Optional<ChangedSchema> diff(HashSet<String> refSet, Components leftComponents, Components rightComponents, Schema left, Schema right, DiffContext context) {
        changedSchema.setContext(context);
        changedSchema.setOldSchema(left);
        changedSchema.setNewSchema(right);
        changedSchema.setChangeDeprecated(!Boolean.TRUE.equals(left.getDeprecated()) && Boolean.TRUE.equals(right.getDeprecated()));
        changedSchema.setChangeDescription(!Objects.equals(left.getDescription(), right.getDescription()));
        changedSchema.setChangeTitle(!Objects.equals(left.getTitle(), right.getTitle()));
        changedSchema.setChangeRequired(ListDiff.diff(left.getRequired(), right.getRequired()));
        changedSchema.setChangeDefault(!Objects.equals(left.getDefault(), right.getDefault()));
        changedSchema.setChangeEnum(ListDiff.diff(left.getEnum(), right.getEnum()));
        changedSchema.setChangeFormat(!Objects.equals(left.getFormat(), right.getFormat()));
        changedSchema.setChangedReadOnly(new ChangedReadOnly(context, left.getReadOnly(), right.getReadOnly()));
        changedSchema.setChangedWriteOnly(new ChangedWriteOnly(context, left.getWriteOnly(), right.getWriteOnly()));
        changedSchema.setChangedMaxLength(!Objects.equals(left.getMaxLength(), right.getMaxLength()));
        Optional<ChangedExtensions> changedExtensions = openApiDiff.getExtensionsDiff().diff(left.getExtensions(), right.getExtensions(), context);
        changedExtensions.ifPresent(changedSchema::setChangedExtensions);

        Map<String, Schema> leftProperties = null == left ? null : left.getProperties();
        Map<String, Schema> rightProperties = null == right ? null : right.getProperties();
        MapKeyDiff<String, Schema> propertyDiff = MapKeyDiff.diff(leftProperties, rightProperties);

        for (String key : propertyDiff.getSharedKey()) {
            Optional<ChangedSchema> resultSchema = openApiDiff.getSchemaDiff().diff(refSet, leftProperties.get(key), rightProperties.get(key), required(context, key, right.getRequired()));
            resultSchema.ifPresent(changedSchema1 -> changedSchema.getChangedProperties().put(key, changedSchema1));
        }

        compareAdditionalProperties(refSet, left, right, context);

        changedSchema.getIncreasedProperties().putAll(filterProperties(propertyDiff.getIncreased(), context));
        changedSchema.getMissingProperties().putAll(filterProperties(propertyDiff.getMissing(), context));
        return isChanged(changedSchema);
    }

    private Map<String, Schema> filterProperties(Map<String, Schema> properties, DiffContext context) {
        return properties.entrySet().stream()
                .filter(entry -> isPropertyApplicable(entry.getValue(), context))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean isPropertyApplicable(Schema schema, DiffContext context) {
        return !(context.isResponse() && Boolean.TRUE.equals(schema.getWriteOnly()))
                && !(context.isRequest() && Boolean.TRUE.equals(schema.getReadOnly()));
    }

    private DiffContext required(DiffContext context, String key, List<String> required) {
        return context.copyWithRequired(required != null && required.contains(key));
    }

    private void compareAdditionalProperties(HashSet<String> refSet, Schema leftSchema, Schema rightSchema, DiffContext context) {
        Object left = leftSchema.getAdditionalProperties();
        Object right = rightSchema.getAdditionalProperties();
        if ((left != null && left instanceof Schema) || (right != null && right instanceof Schema)) {
            Schema leftAdditionalSchema = (Schema) left;
            Schema rightAdditionalSchema = (Schema) right;
            ChangedSchema apChangedSchema = new ChangedSchema();
            apChangedSchema.setContext(context);
            apChangedSchema.setOldSchema(leftAdditionalSchema);
            apChangedSchema.setNewSchema(rightAdditionalSchema);
            if (left != null && right != null) {
                Optional<ChangedSchema> addPropChangedSchemaOP
                        = openApiDiff.getSchemaDiff().diff(refSet, leftAdditionalSchema, rightAdditionalSchema, context.copyWithRequired(false));
                apChangedSchema = addPropChangedSchemaOP.orElse(apChangedSchema);
            }
            isChanged(apChangedSchema).ifPresent(changedSchema::setAddPropChangedSchema);
        }
    }
}
