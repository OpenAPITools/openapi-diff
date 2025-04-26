package org.openapitools.openapidiff.core.model.deferred;

import java.util.HashSet;
import org.openapitools.openapidiff.core.compare.CacheKey;

public class RecursiveSchemaSet {
  HashSet<String> leftKeys = new HashSet<>();
  HashSet<String> rightKeys = new HashSet<>();

  public HashSet<String> getLeftKeys() {
    return leftKeys;
  }

  public HashSet<String> getRightKeys() {
    return rightKeys;
  }

  public boolean contains(CacheKey key) {
    return leftKeys.contains(key.getLeft()) || rightKeys.contains(key.getRight());
  }

  public void put(CacheKey key) {
    leftKeys.add(key.getLeft());
    rightKeys.add(key.getRight());
  }
}
