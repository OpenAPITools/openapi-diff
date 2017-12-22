package com.qdesrame.openapi.diff.compare.schemadiffresult;

import com.qdesrame.openapi.diff.compare.MapKeyDiff;
import com.qdesrame.openapi.diff.compare.SchemaDiff;
import com.qdesrame.openapi.diff.model.ChangedOneOfSchema;
import com.qdesrame.openapi.diff.model.ChangedSchema;
import com.qdesrame.openapi.diff.utils.RefPointer;
import io.swagger.oas.models.Components;
import io.swagger.oas.models.media.ComposedSchema;
import io.swagger.oas.models.media.Discriminator;
import io.swagger.oas.models.media.Schema;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by adarsh.sharma on 20/12/17.
 */
public class ComposedSchemaDiffResult extends SchemaDiffResult {
    @Override
    public ChangedSchema diff(Components leftComponents, Components rightComponents, Schema left, Schema right) {
        ComposedSchema leftComposedSchema = (ComposedSchema) left;
        ComposedSchema rightComposedSchema = (ComposedSchema) right;
        if (CollectionUtils.isNotEmpty(leftComposedSchema.getOneOf())
                || CollectionUtils.isNotEmpty(rightComposedSchema.getOneOf())) {

            Discriminator leftDis = leftComposedSchema.getDiscriminator();
            Discriminator rightDis = rightComposedSchema.getDiscriminator();
            if (leftDis == null || rightDis == null || leftDis.getPropertyName() == null || rightDis.getPropertyName() == null) {
                throw new IllegalArgumentException("discriminator or property not found for oneOf schema");
            } else if (!leftDis.getPropertyName().equals(rightDis.getPropertyName()) ||
                    (CollectionUtils.isEmpty(leftComposedSchema.getOneOf()) || CollectionUtils.isEmpty(rightComposedSchema.getOneOf()))) {
                changedSchema.setOldSchema(left);
                changedSchema.setNewSchema(right);
                changedSchema.setDiscriminatorPropertyChanged(true);
                return changedSchema;
            }

            Map<String, String> leftMapping = getMapping(leftComposedSchema);
            Map<String, String> rightMapping = getMapping(rightComposedSchema);

            ChangedOneOfSchema changedOneOfSchema = new ChangedOneOfSchema(leftMapping, rightMapping);
            MapKeyDiff<String, String> mappingDiff = MapKeyDiff.diff(leftMapping, rightMapping);
            changedOneOfSchema.setIncreasedMapping(mappingDiff.getIncreased());
            changedOneOfSchema.setMissingMapping(mappingDiff.getMissing());

            Map<String, ChangedSchema> changedMapping = new HashMap<>();
            changedOneOfSchema.setChangedMapping(changedMapping);

            for (String key : mappingDiff.getSharedKey()) {
                Schema leftSchema = new Schema();
                leftSchema.set$ref(leftMapping.get(key));
                Schema rightSchema = new Schema();
                rightSchema.set$ref(rightMapping.get(key));
                ChangedSchema changedSchema = SchemaDiff.fromComponents(leftComponents, rightComponents).diff(leftSchema, rightSchema);
                if (changedSchema.isDiff()) {
                    changedMapping.put(key, changedSchema);
                }
            }
            changedSchema.setChangedOneOfSchema(changedOneOfSchema);
        }
        return super.processDiff(leftComponents, rightComponents, left, right);
    }

    private Map<String, String> getMapping(ComposedSchema composedSchema) {
        Map<String, String> reverseMapping = new HashMap<>();
        for (Schema schema : composedSchema.getOneOf()) {
            String ref = schema.get$ref();
            if (ref == null) {
                throw new IllegalArgumentException("invalid oneOf schema");
            }
            String schemaName = RefPointer.getSchemaName(ref);
            if (schemaName == null) {
                throw new IllegalArgumentException("invalid schema: " + ref);
            }
            reverseMapping.put(ref, schemaName);
        }

        if (composedSchema.getDiscriminator().getMapping() != null) {
            for (String ref : composedSchema.getDiscriminator().getMapping().keySet()) {
                reverseMapping.put(composedSchema.getDiscriminator().getMapping().get(ref), ref);
            }
        }

        return reverseMapping.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

}
