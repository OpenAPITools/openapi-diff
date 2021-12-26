package org.openapitools.openapidiff.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;

class ParametersOverloadingTest {

    private final String OVERLOADED_PARAMETERS = "parameters_overloading.yaml";
    private final String DUPLICATED_PARAMETER_TYPES = "parameters_overloading_2.yaml";

    @Test
    void testDiffWithOverloadedParameterTypes() {
        assertDoesNotThrow(() -> OpenApiCompare.fromLocations(OVERLOADED_PARAMETERS, OVERLOADED_PARAMETERS));
        assertOpenApiAreEquals(OVERLOADED_PARAMETERS, OVERLOADED_PARAMETERS);
    }

    @Test
    void testDiffWithDuplicatedParameterTypes() {
        assertThrows(
                IllegalArgumentException.class,
                () -> OpenApiCompare.fromLocations(DUPLICATED_PARAMETER_TYPES, DUPLICATED_PARAMETER_TYPES),
                "Two path items have the same signature: /projects/{}");
    }
}