package com.qdesrame.openapi.diff.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class Copy {
  public static <K, V> Map<K, V> copyMap(Map<K, V> map) {
    if (map == null) {
      return new LinkedHashMap<>();
    } else {
      return new LinkedHashMap<>(map);
    }
  }
}
