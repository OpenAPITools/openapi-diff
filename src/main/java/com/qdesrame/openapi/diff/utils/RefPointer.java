package com.qdesrame.openapi.diff.utils;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;

public class RefPointer {

    public final static String BASE_REF = "#/components/";
    public final static String REQUEST_BODIES = "requestBodies";
    public final static String RESPONSES = "responses";
    public final static String PARAMETERS = "parameters";
    public final static String SCHEMAS = "schemas";
    public final static String HEADERS = "headers";
    public final static String SECURITY_SCHEMES = "securitySchemes";

    public static Parameter parameter(Components components, String ref) {
        Parameter result = components.getParameters().get(getRefName(PARAMETERS, ref));
        if (result == null) {
            throw new IllegalArgumentException(String.format("Parameter for ref '%s' doesn't exist.", ref));
        }
        return result;
    }

    public static Schema schema(Components components, String ref) {
        Schema result = components.getSchemas().get(getRefName(SCHEMAS, ref));
        if (result == null) {
            throw new IllegalArgumentException(String.format("Schema for ref '%s' doesn't exist.", ref));
        }
        return result;
    }

    public static RequestBody requestBody(Components components, String ref) {
        RequestBody result = components.getRequestBodies().get(getRefName(REQUEST_BODIES, ref));
        if (result == null) {
            throw new IllegalArgumentException(String.format("Request body for ref '%s' doesn't exist.", ref));
        }
        return result;
    }

    public static ApiResponse response(Components components, String ref) {
        ApiResponse result = components.getResponses().get(getRefName(RESPONSES, ref));
        if (result == null) {
            throw new IllegalArgumentException(String.format("Response for ref '%s' doesn't exist.", ref));
        }
        return result;
    }

    public static Header header(Components components, String ref) {
        Header result = components.getHeaders().get(getRefName(HEADERS, ref));
        if (result == null) {
            throw new IllegalArgumentException(String.format("Header for ref '%s' doesn't exist.", ref));
        }
        return result;
    }

    public static SecurityScheme securityScheme(Components components, String ref) {
        SecurityScheme result = components.getSecuritySchemes().get(getRefName(SECURITY_SCHEMES, ref));
        if (result == null) {
            throw new IllegalArgumentException(String.format("Security Scheme for ref '%s' doesn't exist.", ref));
        }
        return result;
    }

    public static String getSchemaName(String ref) {
        if (ref != null) {
            return RefPointer.getRefName(SCHEMAS, ref);
        }
        return null;
    }

    protected static String getBaseRefForType(String type) {
        return String.format("%s%s/", BASE_REF, type);
    }

    protected static String getRefName(String type, String ref) {
        final String baseRef = getBaseRefForType(type);
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

        public static RequestBody requestBody(Components components, RequestBody requestBody) {
            if (requestBody.get$ref() != null) {
                requestBody = RefPointer.requestBody(components, requestBody.get$ref());
            }
            return requestBody;
        }

        public static ApiResponse response(Components components, ApiResponse response) {
            if (response.get$ref() != null) {
                response = RefPointer.response(components, response.get$ref());
            }
            return response;
        }

        public static Header header(Components components, Header header) {
            if (header.get$ref() != null) {
                header = RefPointer.header(components, header.get$ref());
            }
            return header;
        }

        public static SecurityScheme securityScheme(Components components, SecurityScheme securityScheme) {
            if (securityScheme.get$ref() != null) {
                securityScheme = RefPointer.securityScheme(components, securityScheme.get$ref());
            }
            return securityScheme;
        }
    }
}
