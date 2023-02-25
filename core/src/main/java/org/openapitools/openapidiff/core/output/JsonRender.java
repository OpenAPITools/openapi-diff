package org.openapitools.openapidiff.core.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Paths;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class JsonRender implements Render {
  private final ObjectMapper objectMapper;

  public JsonRender() {
    objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.findAndRegisterModules();
  }

  @Override
  public String render(ChangedOpenApi diff) {
    try {
      return objectMapper.writeValueAsString(diff);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not serialize diff as JSON", e);
    }
  }

  public void renderToFile(ChangedOpenApi diff, String file) {
    try {
      objectMapper.writeValue(Paths.get(file).toFile(), diff);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not serialize diff as JSON", e);
    } catch (IOException e) {
      throw new RuntimeException("Could not write to JSON file", e);
    }
  }
}
