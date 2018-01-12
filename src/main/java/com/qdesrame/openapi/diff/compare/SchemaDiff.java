package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.compare.schemadiffresult.ArraySchemaDiffResult;
import com.qdesrame.openapi.diff.compare.schemadiffresult.ComposedSchemaDiffResult;
import com.qdesrame.openapi.diff.compare.schemadiffresult.SchemaDiffResult;
import com.qdesrame.openapi.diff.model.ChangedSchema;
import com.qdesrame.openapi.diff.utils.RefPointer;
import com.qdesrame.openapi.diff.utils.RefType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.*;

public class SchemaDiff extends ReferenceDiffCache<Schema, ChangedSchema> {

    private Components leftComponents;
    private Components rightComponents;
    private OpenApiDiff openApiDiff;
    private static RefPointer<Schema> refPointer = new RefPointer<>(RefType.SCHEMAS);

    private static Map<Class<? extends Schema>, Class<? extends SchemaDiffResult>> schemaDiffResultClassMap = new HashMap<>();

    static {
        schemaDiffResultClassMap.put(Schema.class, SchemaDiffResult.class);
        schemaDiffResultClassMap.put(ArraySchema.class, ArraySchemaDiffResult.class);
        schemaDiffResultClassMap.put(ComposedSchema.class, ComposedSchemaDiffResult.class);
    }

    public static SchemaDiffResult getSchemaDiffResult(OpenApiDiff openApiDiff) {
        return getSchemaDiffResult(null, openApiDiff);
    }

    public static SchemaDiffResult getSchemaDiffResult(Class<? extends Schema> classType, OpenApiDiff openApiDiff) {
        if (classType == null) {
            classType = Schema.class;
        }

        Class<? extends SchemaDiffResult> aClass = schemaDiffResultClassMap.get(classType);
        try {
            if (aClass == null) {
                aClass = schemaDiffResultClassMap.get(Schema.class);
            }
            if (aClass != null) {
                return aClass.getConstructor(OpenApiDiff.class).newInstance(openApiDiff);
            } else {
                throw new IllegalArgumentException("invalid classType");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("type " + classType + " is illegal");
        }
    }

    public SchemaDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.leftComponents = openApiDiff.getOldSpecOpenApi() != null ? openApiDiff.getOldSpecOpenApi().getComponents() : null;
        this.rightComponents = openApiDiff.getNewSpecOpenApi() != null ? openApiDiff.getNewSpecOpenApi().getComponents() : null;
    }

    public Optional<ChangedSchema> diff(Schema left, Schema right) {
        return super.cachedDiff(left, right, left.get$ref(), right.get$ref());
    }

    @Override
    protected Optional<ChangedSchema> computeDiff(Schema left, Schema right) {
        left = refPointer.resolveRef(this.leftComponents, left, left.get$ref());
        right = refPointer.resolveRef(this.rightComponents, right, right.get$ref());

        left = resolveComposedSchema(leftComponents, left);
        right = resolveComposedSchema(rightComponents, right);

        //If type of schemas are different, just set old & new schema, set changedType to true in SchemaDiffResult and
        // return the object
        if (!Objects.equals(left.getType(), right.getType()) ||
                !Objects.equals(left.getFormat(), right.getFormat())) {
            ChangedSchema changedSchema = SchemaDiff.getSchemaDiffResult(openApiDiff).getChangedSchema();
            changedSchema.setOldSchema(left);
            changedSchema.setNewSchema(right);
            changedSchema.setChangedType(true);
            return Optional.of(changedSchema);
        }

        //If schema type is same then get specific SchemaDiffResult and compare the properties
        SchemaDiffResult result = SchemaDiff.getSchemaDiffResult(right.getClass(), openApiDiff);
        return result.diff(leftComponents, rightComponents, left, right);
    }

    protected static Schema resolveComposedSchema(Components components, Schema schema) {
        if (schema instanceof ComposedSchema) {
            ComposedSchema composedSchema = (ComposedSchema) schema;
            List<Schema> allOfSchemaList = composedSchema.getAllOf();
            if (allOfSchemaList != null) {
                for (Schema allOfSchema : allOfSchemaList) {
                    allOfSchema = refPointer.resolveRef(components, allOfSchema, allOfSchema.get$ref());
                    allOfSchema = resolveComposedSchema(components, allOfSchema);
                    schema = addSchema(schema, allOfSchema);
                }
                composedSchema.setAllOf(null);
            }
        }
        return schema;
    }

    protected static Schema addSchema(Schema schema, Schema fromSchema) {
        if (fromSchema.getProperties() != null) {
            if (schema.getProperties() == null) {
                schema.setProperties(fromSchema.getProperties());
            } else {
                schema.getProperties().putAll(fromSchema.getProperties());
            }
        }

        if (fromSchema.getRequired() != null) {
            if (schema.getRequired() == null) {
                schema.setRequired(fromSchema.getRequired());
            } else {
                schema.getRequired().addAll(fromSchema.getRequired());
            }
        }
        //TODO copy other things from fromSchema
        return schema;
    }
}
