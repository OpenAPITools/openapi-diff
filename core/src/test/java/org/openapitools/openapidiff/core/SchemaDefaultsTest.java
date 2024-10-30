package org.openapitools.openapidiff.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedSchema;

public class SchemaDefaultsTest {

    @Test
    public void issue717DefaultsInSchema() {
        ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(
            "issue-717-schema-defaults-handling-1.yaml",
            "issue-717-schema-defaults-handling-2.yaml"
        );

        assertEquals(1, changedOpenApi.getChangedOperations().size());
        assertEquals(1, changedOpenApi.getChangedSchemas().size());
        ChangedSchema changedSchema = changedOpenApi.getChangedSchemas().get(0);
        assertEquals(1, changedSchema.getChangedProperties().size());
        assertTrue(changedSchema.getChangedProperties().containsKey("field1"));
        assertTrue(changedSchema.getChangedProperties().get("field1").isChangeDefault());
    }
}
