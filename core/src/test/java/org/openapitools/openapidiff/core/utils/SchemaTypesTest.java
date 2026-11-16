package org.openapitools.openapidiff.core.utils;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.media.JsonSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** The helper reads both shapes the engine sees while the 3.1 phase is in progress (#950). */
class SchemaTypesTest {

  @Test
  void readsThe30TypeString() {
    Schema<?> schema = new StringSchema();
    assertThat(SchemaTypes.types(schema)).containsExactly("string");
    assertThat(SchemaTypes.hasNullType(schema)).isFalse();
  }

  @Test
  void readsThe31TypesSet() {
    JsonSchema schema = new JsonSchema();
    schema.setTypes(new LinkedHashSet<>(Set.of("integer", "string")));
    assertThat(SchemaTypes.types(schema)).containsExactlyInAnyOrder("integer", "string");
    assertThat(SchemaTypes.hasNullType(schema)).isFalse();
  }

  @Test
  void splitsNullOutOfTheTypesSet() {
    JsonSchema schema = new JsonSchema();
    schema.setTypes(new LinkedHashSet<>(Set.of("string", SchemaTypes.NULL_TYPE)));
    assertThat(SchemaTypes.types(schema)).containsExactly("string");
    assertThat(SchemaTypes.hasNullType(schema)).isTrue();
  }

  @Test
  void unionsBothShapesWhenBothAreSet() {
    Schema<?> schema = new Schema<>();
    schema.setType("string");
    schema.setTypes(new LinkedHashSet<>(Set.of("integer")));
    assertThat(SchemaTypes.types(schema)).containsExactlyInAnyOrder("string", "integer");
  }

  @Test
  void anUntypedSchemaComparesAsEmpty() {
    assertThat(SchemaTypes.types(new Schema<>())).isEmpty();
  }

  @Test
  void nullableValueKeepsTheRaw30Flag() {
    Schema<?> schema = new StringSchema();
    schema.setNullable(false);
    assertThat(SchemaTypes.nullableValue(schema)).isFalse();
    schema.setNullable(true);
    assertThat(SchemaTypes.nullableValue(schema)).isTrue();
  }

  @Test
  void nullableValueReadsThe31NullType() {
    JsonSchema schema = new JsonSchema();
    assertThat(SchemaTypes.nullableValue(schema)).isNull();
    schema.setTypes(new LinkedHashSet<>(Set.of("string", SchemaTypes.NULL_TYPE)));
    assertThat(SchemaTypes.nullableValue(schema)).isTrue();
  }

  @Test
  void theRawNullableFlagWinsOverTheNullType() {
    JsonSchema schema = new JsonSchema();
    schema.setNullable(false);
    schema.setTypes(new LinkedHashSet<>(Set.of("string", SchemaTypes.NULL_TYPE)));
    assertThat(SchemaTypes.nullableValue(schema)).isFalse();
  }
}
