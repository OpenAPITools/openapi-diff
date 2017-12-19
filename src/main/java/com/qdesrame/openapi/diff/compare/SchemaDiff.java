package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.compare.schemaDiffResult.ArraySchemaDiffResult;
import com.qdesrame.openapi.diff.compare.schemaDiffResult.SchemaDiffResult;
import io.swagger.oas.models.Components;
import io.swagger.oas.models.media.ArraySchema;
import io.swagger.oas.models.media.Schema;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SchemaDiff {

    private Components leftComponents;
    private Components rightComponents;
    private static Map<Class<? extends Schema>, Class<? extends SchemaDiffResult>> schemaDiffResultClassMap = new HashMap<>();

    static {
        schemaDiffResultClassMap.put(Schema.class, SchemaDiffResult.class);
        schemaDiffResultClassMap.put(ArraySchema.class, ArraySchemaDiffResult.class);
        //TODO add other classes for different schema types
    }

    public static SchemaDiffResult getSchemaDiffResult() {
        return getSchemaDiffResult(null);
    }

    public static SchemaDiffResult getSchemaDiffResult(Class<? extends Schema> classType) {
        if (classType == null) {
            throw new IllegalArgumentException("classType can not be null");
        }

        Class<? extends SchemaDiffResult> aClass = schemaDiffResultClassMap.get(classType);
        try {
            if (aClass == null) {
                aClass = schemaDiffResultClassMap.get(Schema.class);
            }
            if (aClass != null) {
                Class<? extends SchemaDiffResult> schemaDiffResultClass = aClass;
                Constructor<? extends SchemaDiffResult> constructor = schemaDiffResultClass.getConstructor();
                return constructor.newInstance();
            } else {
                throw new IllegalArgumentException("invalid classType");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("type " + classType + " is illegal");
        }
    }

    private SchemaDiff(Components left, Components right) {
        this.leftComponents = left;
        this.rightComponents = right;
    }

    public static SchemaDiff fromComponents(Components left, Components right) {
        return new SchemaDiff(left, right);
    }

    public SchemaDiffResult diff(Schema left, Schema right) {
        //If type of schemas are different, just set old & new schema, set changedType to true in SchemaDiffResult and
        // return the object
        if (!Objects.equals(left.getType(), right.getType()) ||
                !Objects.equals(left.getFormat(), right.getFormat())) {
            SchemaDiffResult result = SchemaDiff.getSchemaDiffResult();
            result.setOldSchema(left);
            result.setNewSchema(right);
            result.setChangedType(true);
            return result;
        }

        //If schema type is same then get specific SchemaDiffResult and compare the properties
        SchemaDiffResult result = SchemaDiff.getSchemaDiffResult(right.getClass());
        result.diff(leftComponents, rightComponents, left, right);
        return result;
    }

}
