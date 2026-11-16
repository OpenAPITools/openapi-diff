package org.openapitools.openapidiff.core.utils;

import io.swagger.v3.oas.models.media.Schema;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Reads the type keyword from the two shapes the engine sees while the OpenAPI 3.1 phase is in
 * progress. In 3.0 (and Swagger 2.0) documents the parser stores the type in {@link
 * Schema#getType()}; in 3.1 documents the type keyword lands in the {@link Schema#getTypes()} set
 * and {@code getType()} is null. The helpers in this class accept both shapes and split the keyword
 * into the two things the engine reasons about: the set of non-null types, and whether null is one
 * of them. While the phase is in progress both shapes are accepted, so 3.0 documents keep their
 * current verdicts.
 */
public class SchemaTypes {

  /** The JSON Schema 2020-12 type value that stands for null. */
  public static final String NULL_TYPE = "null";

  private SchemaTypes() {}

  /**
   * The effective set of non-null types of a schema: the 3.0 {@code type} string and the 3.1 {@code
   * types} entries, minus {@value #NULL_TYPE}. "null" never appears here; nullability goes through
   * the null rule ({@link #nullableValue(Schema)}) instead. Returns an empty set when the schema
   * carries no type keyword, so two untyped schemas compare as equal.
   */
  public static Set<String> types(Schema<?> schema) {
    Set<String> result = new LinkedHashSet<>();
    if (schema.getType() != null) {
      result.add(schema.getType());
    }
    if (schema.getTypes() != null) {
      for (String type : schema.getTypes()) {
        if (type != null && !NULL_TYPE.equals(type)) {
          result.add(type);
        }
      }
    }
    return result;
  }

  /** Whether the 3.1 types set contains {@value #NULL_TYPE}. */
  public static boolean hasNullType(Schema<?> schema) {
    return schema.getTypes() != null && schema.getTypes().contains(NULL_TYPE);
  }

  /**
   * The value the null rule compares: the 3.0 {@code nullable} flag when it is set, otherwise
   * whether {@value #NULL_TYPE} is one of the 3.1 types. Returns null when the schema says nothing
   * about nullability, so the compared values keep the raw nullable flag exactly as before for 3.0
   * documents.
   */
  public static Boolean nullableValue(Schema<?> schema) {
    if (schema.getNullable() != null) {
      return schema.getNullable();
    }
    return hasNullType(schema) ? Boolean.TRUE : null;
  }
}
