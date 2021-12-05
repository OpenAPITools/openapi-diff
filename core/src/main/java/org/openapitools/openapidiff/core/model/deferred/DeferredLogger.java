package org.openapitools.openapidiff.core.model.deferred;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ComposedChanged;

public class DeferredLogger {

  public static <T> Object logValue(Object value) {
    return new Object() {
      public String toString() {
        return valueToString(value);
      }
    };
  }

  public static String optionalToString(Optional<?> value) {
    return value.map((v) -> "Optional[" + valueToString(v) + "]").orElse("Optional[empty]");
  }

  public static String changedToString(Changed value) {
    if (value instanceof ComposedChanged) {
      return "Changed: " + value.getClass() + " (composed) ";
    } else {
      return "Changed: " + value.getClass() + " " + value.isChanged();
    }
  }

  public static String streamToString(Stream<?> values) {
    return "[" + values.map(v -> valueToString(v)).collect(Collectors.joining(", ")) + "]";
  }

  public static String deferredChangeToString(DeferredChanged<?> deferredChanged) {
    if (deferredChanged.isValueSet()) {
      if (deferredChanged.isPresent()) {
        Object value = deferredChanged.get();
        return valueToString(value);
      } else {
        return deferredChanged.toString();
      }
    } else {
      return deferredChanged.toString();
    }
  }

  public static String valueToString(Object value) {
    if (value == null) {
      return "null";
    } else if (value instanceof Changed) {
      return changedToString((Changed) value);
    } else if (value instanceof Optional) {
      return optionalToString((Optional) value);
    } else if (value instanceof DeferredChanged) {
      return deferredChangeToString((DeferredChanged) value);
    } else if (value.getClass().isArray()) {
      return streamToString(Arrays.stream((Object[]) value));
    } else if (value instanceof Collection) {
      return streamToString(((Collection) value).stream());
    } else {
      return value.toString();
    }
  }
}
