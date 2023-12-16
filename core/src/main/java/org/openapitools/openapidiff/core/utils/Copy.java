package org.openapitools.openapidiff.core.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class Copy {

  private Copy() {
    throw new UnsupportedOperationException("Utility class. Do not instantiate");
  }

  public static <K, V> Map<K, V> copyMap(Map<K, V> map) {
    if (map == null) {
      return new LinkedHashMap<>();
    } else {
      return new LinkedHashMap<>(map);
    }
  }
}
