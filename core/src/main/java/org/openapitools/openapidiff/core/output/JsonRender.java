package org.openapitools.openapidiff.core.output;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class JsonRender implements Render {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String render(ChangedOpenApi diff) {
        try {
            return objectMapper.writeValueAsString(diff);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not serialize diff as JSON", e);
        }
    }
}
