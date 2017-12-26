package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.compare.schemadiffresult.ArraySchemaDiffResult;
import com.qdesrame.openapi.diff.compare.schemadiffresult.ComposedSchemaDiffResult;
import com.qdesrame.openapi.diff.compare.schemadiffresult.SchemaDiffResult;
import com.qdesrame.openapi.diff.model.ChangedSchema;
import com.qdesrame.openapi.diff.utils.RefPointer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SchemaDiff {

    private Components leftComponents;
    private Components rightComponents;
    private OpenApiDiff openApiDiff;
    private SchemaDiffCache schemaDiffCache;
    private static Map<Class<? extends Schema>, Class<? extends SchemaDiffResult>> schemaDiffResultClassMap = new HashMap<>();

    static {
        schemaDiffResultClassMap.put(Schema.class, SchemaDiffResult.class);
        schemaDiffResultClassMap.put(ArraySchema.class, ArraySchemaDiffResult.class);
        schemaDiffResultClassMap.put(ComposedSchema.class, ComposedSchemaDiffResult.class);
        //TODO add other classes for different schema types
    }

    public static SchemaDiffResult getSchemaDiffResult(OpenApiDiff openApiDiff) {
        return getSchemaDiffResult(null, openApiDiff);
    }

    public static SchemaDiffResult getSchemaDiffResult(Class<? extends Schema> classType, OpenApiDiff openApiDiff) {
        if (classType == null) {
            throw new IllegalArgumentException("classType can not be null");
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
        this.schemaDiffCache = new SchemaDiffCache();
    }

    public ChangedSchema diff(Schema left, Schema right) {
        String leftRef = left.get$ref();
        String rightRef = right.get$ref();
        boolean areBothRefSchemas = leftRef != null && rightRef != null;
        if (areBothRefSchemas) {
            ChangedSchema changedSchemaFromCache = schemaDiffCache.getFromCache(leftRef, rightRef);
            if (changedSchemaFromCache != null) {
                return changedSchemaFromCache;
            }
        }

        left = RefPointer.Replace.schema(leftComponents, left);
        right = RefPointer.Replace.schema(rightComponents, right);

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
            return changedSchema;
        }

        //If schema type is same then get specific SchemaDiffResult and compare the properties
        SchemaDiffResult result = SchemaDiff.getSchemaDiffResult(right.getClass(), openApiDiff);
        ChangedSchema changedSchema = result.diff(leftComponents, rightComponents, left, right);
        if(areBothRefSchemas) {
            schemaDiffCache.addToCache(leftRef, rightRef, changedSchema);
        }
        return changedSchema;
    }

    public static Schema resolveComposedSchema(Components components, Schema schema) {
        if (schema instanceof ComposedSchema) {
            ComposedSchema composedSchema = (ComposedSchema) schema;
            List<Schema> allOfSchemaList = composedSchema.getAllOf();
            if (allOfSchemaList != null) {
                for (Schema allOfSchema : allOfSchemaList) {
                    allOfSchema = RefPointer.Replace.schema(components, allOfSchema);
                    allOfSchema = resolveComposedSchema(components, allOfSchema);
                    schema = addSchema(schema, allOfSchema);
                }
            }
        }
        return schema;
    }

    private static Schema addSchema(Schema schema, Schema fromSchema) {
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
