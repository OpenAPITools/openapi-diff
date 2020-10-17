package org.openapitools.openapidiff.core.model;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ChangedMediaType implements ComposedChanged {
  private final Schema oldSchema;
  private final Schema newSchema;
  private final DiffContext context;
  private ChangedSchema schema;

  public ChangedMediaType(Schema oldSchema, Schema newSchema, DiffContext context) {
    this.oldSchema = oldSchema;
    this.newSchema = newSchema;
    this.context = context;
  }

  @Override
  public List<Changed> getChangedElements() {
    return Collections.singletonList(schema);
  }

  @Override
  public DiffResult isCoreChanged() {
    return DiffResult.NO_CHANGES;
  }

  public Schema getOldSchema() {
    return this.oldSchema;
  }

  public Schema getNewSchema() {
    return this.newSchema;
  }

  public DiffContext getContext() {
    return this.context;
  }

  public ChangedSchema getSchema() {
    return this.schema;
  }

  public ChangedMediaType setSchema(final ChangedSchema schema) {
    this.schema = schema;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedMediaType that = (ChangedMediaType) o;
    return Objects.equals(oldSchema, that.oldSchema)
        && Objects.equals(newSchema, that.newSchema)
        && Objects.equals(context, that.context)
        && Objects.equals(schema, that.schema);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oldSchema, newSchema, context, schema);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedMediaType(oldSchema="
        + this.getOldSchema()
        + ", newSchema="
        + this.getNewSchema()
        + ", context="
        + this.getContext()
        + ", schema="
        + this.getSchema()
        + ")";
  }
}
