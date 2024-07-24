package org.openapitools.openapidiff.core.model;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChangedMediaType implements ComposedChanged {
  private final Schema oldSchema;
  private final Schema newSchema;
  private final DiffContext context;
  private ChangedSchema schema;
  private ChangedExamples examples;
  private ChangedExample example;

  public ChangedMediaType(Schema oldSchema, Schema newSchema, DiffContext context) {
    this.oldSchema = oldSchema;
    this.newSchema = newSchema;
    this.context = context;
  }

  @Override
  public List<Changed> getChangedElements() {
    return Arrays.asList(schema, examples, example);
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

  public ChangedExamples getExamples() {
    return this.examples;
  }

  public ChangedExample getExample() {
    return this.example;
  }

  public ChangedMediaType setSchema(final ChangedSchema schema) {
    this.schema = schema;
    return this;
  }

  public ChangedMediaType setExamples(final ChangedExamples examples) {
    this.examples = examples;
    return this;
  }

  public ChangedMediaType setExample(final ChangedExample example) {
    this.example = example;
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
        && Objects.equals(schema, that.schema)
        && Objects.equals(examples, that.examples)
        && Objects.equals(example, that.example);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oldSchema, newSchema, context, schema, examples, example);
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
        + ", examples="
        + this.getExamples()
        + ", example="
        + this.getExample()
        + ")";
  }
}
