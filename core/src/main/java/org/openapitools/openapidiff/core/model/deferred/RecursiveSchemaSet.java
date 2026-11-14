package org.openapitools.openapidiff.core.model.deferred;

import io.swagger.v3.oas.models.media.Schema;
import java.util.HashSet;
import org.openapitools.openapidiff.core.compare.CacheKey;

public class RecursiveSchemaSet {
  HashSet<String> leftKeys = new HashSet<>();
  HashSet<String> rightKeys = new HashSet<>();
  HashSet<SchemaPair> schemaPath = new HashSet<>();

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

  // Resolving an allOf copies the target's properties into the wrapper and then clears the allOf,
  // so a cycle between two such wrappers ends up with no $ref left on either one and is invisible
  // to the key-based guards above. Identity is all that still distinguishes them.
  public boolean enter(Schema<?> left, Schema<?> right) {
    return schemaPath.add(new SchemaPair(left, right));
  }

  public void leave(Schema<?> left, Schema<?> right) {
    schemaPath.remove(new SchemaPair(left, right));
  }

  private static final class SchemaPair {
    private final Schema<?> left;
    private final Schema<?> right;

    private SchemaPair(Schema<?> left, Schema<?> right) {
      this.left = left;
      this.right = right;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof SchemaPair)) return false;
      SchemaPair other = (SchemaPair) o;
      return left == other.left && right == other.right;
    }

    @Override
    public int hashCode() {
      return 31 * System.identityHashCode(left) + System.identityHashCode(right);
    }
  }
}
