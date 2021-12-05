package org.openapitools.openapidiff.core.utils;

import java.util.Optional;
import org.openapitools.openapidiff.core.model.Changed;

public class ChangedUtils {

  private ChangedUtils() {}

  public static boolean isUnchanged(Changed changed) {
    return changed == null || changed.isUnchanged();
  }

  public static boolean isCompatible(Changed changed) {
    return changed == null || changed.isCompatible();
  }

  public static <T extends Changed> Optional<T> isChanged(T changed) {
    if (isUnchanged(changed)) {
      return Optional.empty();
    }
    return Optional.of(changed);
  }
}
