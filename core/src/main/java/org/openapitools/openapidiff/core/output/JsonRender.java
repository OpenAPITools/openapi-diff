package org.openapitools.openapidiff.core.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.openapitools.openapidiff.core.exception.RendererException;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class JsonRender implements Render {
  private final ObjectMapper objectMapper;

  public JsonRender() {
    objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.findAndRegisterModules();
  }

  @Override
  public void render(ChangedOpenApi diff, OutputStreamWriter outputStreamWriter) {
    try {
      objectMapper.writeValue(outputStreamWriter, diff);
      outputStreamWriter.close();
    } catch (JsonProcessingException e) {
      throw new RendererException("Could not serialize diff as JSON", e);
    } catch (IOException e) {
      throw new RendererException(e);
    }
  }
}
