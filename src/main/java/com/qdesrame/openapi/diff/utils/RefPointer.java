package com.qdesrame.openapi.diff.utils;

import io.swagger.oas.models.Components;
import io.swagger.oas.models.media.Schema;
import io.swagger.oas.models.parameters.Parameter;

public class RefPointer {

    public final static String BASE_REF = "#/components/";

    public static Parameter parameter(Components components, String ref) {
        Parameter result = components.getParameters().get(getRefName(ref));
        if (result == null) {
            throw new IllegalArgumentException(String.format("Parameter for ref '%s' doesn't exist.", ref));
        }
        return result;
    }

    public static Schema schema(Components components, String ref) {
        Schema result = components.getSchemas().get(getRefName(ref));
        if (result == null) {
            throw new IllegalArgumentException(String.format("Schema for ref '%s' doesn't exist.", ref));
        }
        return result;
    }

    protected static String getBaseRefForType(String type) {
        return String.format("%s%s/", BASE_REF, type);
    }

    protected static String getRefName(String ref) {
        final String baseRef = getBaseRefForType("parameters");
        if (!ref.startsWith(baseRef)) {
            throw new IllegalArgumentException("Invalid ref: " + ref);
        }
        return ref.substring(baseRef.length());
    }

    public static class Replace {
        public static Parameter parameter(Components components, Parameter parameter) {
            if (parameter.get$ref() != null) {
                parameter = RefPointer.parameter(components, parameter.get$ref());
            }
            return parameter;
        }

        public static Schema schema(Components components, Schema schema) {
            if (schema.get$ref() != null) {
                schema = RefPointer.schema(components, schema.get$ref());
            }
            return schema;
        }
    }
}
