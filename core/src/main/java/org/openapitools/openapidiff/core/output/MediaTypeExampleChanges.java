package org.openapitools.openapidiff.core.output;

import static org.openapitools.openapidiff.core.model.Changed.result;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Json;
import java.util.LinkedHashMap;
import java.util.Map;
import org.openapitools.openapidiff.core.exception.RendererException;
import org.openapitools.openapidiff.core.model.ChangedExample;
import org.openapitools.openapidiff.core.model.ChangedExamples;
import org.openapitools.openapidiff.core.model.ChangedMediaType;

/** Example details shared by the text and HTML reports; markup is handled by each renderer. */
final class MediaTypeExampleChanges {
  private MediaTypeExampleChanges() {}

  static Map<String, String> render(ChangedMediaType mediaType) {
    Map<String, String> details = new LinkedHashMap<>();
    ChangedExample example = mediaType.getExample();
    if (result(example).isDifferent()) {
      add(details, "example", example.getLeftExample(), example.getRightExample());
    }
    ChangedExamples examples = mediaType.getExamples();
    if (result(examples).isDifferent()) {
      add(details, "examples", examples.getLeftExamples(), examples.getRightExamples());
    }
    return details;
  }

  private static void add(
      Map<String, String> details, String name, Object oldValue, Object newValue) {
    if (oldValue == null) {
      details.put("Added " + name, json(newValue));
    } else if (newValue == null) {
      details.put("Removed " + name, json(oldValue));
    } else {
      details.put("Old " + name, json(oldValue));
      details.put("New " + name, json(newValue));
    }
  }

  private static String json(Object value) {
    try {
      return Json.mapper()
          .writerWithDefaultPrettyPrinter()
          .writeValueAsString(value)
          .replace("\r\n", "\n");
    } catch (JsonProcessingException e) {
      throw new RendererException("Could not serialize media type example", e);
    }
  }
}
