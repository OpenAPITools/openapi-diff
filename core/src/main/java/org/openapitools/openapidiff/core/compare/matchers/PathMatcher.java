package org.openapitools.openapidiff.core.compare.matchers;

import io.swagger.v3.oas.models.PathItem;
import java.util.Map;
import java.util.Optional;

/** Strategy to find a matching path. */
public interface PathMatcher {

  /**
   * Finds a matching path entry.
   *
   * @param what entry of the path to find
   * @param candidates map of right spec paths to search in
   * @return Optional entry of the matching right path
   */
  Optional<Map.Entry<String, PathItem>> find(
      Map.Entry<String, PathItem> what, Map<String, PathItem> candidates);
}
