package org.openapitools.openapidiff.core.output;

import static org.openapitools.openapidiff.core.model.Changed.result;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Json;
import java.util.LinkedHashMap;
import java.util.Map;
import org.openapitools.openapidiff.core.model.ChangedExample;
import org.openapitools.openapidiff.core.model.ChangedExamples;
import org.openapitools.openapidiff.core.model.ChangedMediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Example details shared by the text and HTML reports; markup is handled by each renderer. */
final class MediaTypeExampleChanges {
  private static final Logger LOGGER = LoggerFactory.getLogger(MediaTypeExampleChanges.class);

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
      LOGGER.warn(
          "Could not serialize media type example as JSON ({}); rendering a placeholder",
          e.getClass().getSimpleName());
      // Do not call toString() on arbitrary example objects: it can fail as well.
      return "\"[Example could not be serialized as JSON]\"";
    }
  }
}
