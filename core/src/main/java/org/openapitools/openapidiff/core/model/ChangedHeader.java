package org.openapitools.openapidiff.core.model;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_HEADER_EXPLODE_CHANGED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_HEADER_REQUIRED_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_HEADER_REQUIRED_INCREASED;

import io.swagger.v3.oas.models.headers.Header;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChangedHeader implements ComposedChanged {
  private final Header oldHeader;
  private final Header newHeader;
  private final DiffContext context;
  private boolean required;
  private boolean deprecated;
  private boolean style;
  private boolean explode;
  private ChangedMetadata description;
  private ChangedExamples examples;
  private ChangedExample example;
  private ChangedSchema schema;
  private ChangedContent content;
  private ChangedExtensions extensions;

  public ChangedHeader(Header oldHeader, Header newHeader, DiffContext context) {
    this.oldHeader = oldHeader;
    this.newHeader = newHeader;
    this.context = context;
  }

  @Override
  public List<Changed> getChangedElements() {
    return Arrays.asList(description, example, examples, schema, content, extensions);
  }

  @Override
  public DiffResult isCoreChanged() {
    if (!required && !deprecated && !style && !explode) {
      return DiffResult.NO_CHANGES;
    }
    if (explode) {
      if (RESPONSE_HEADER_EXPLODE_CHANGED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    if (required) {
      boolean requiredOld = oldHeader.getRequired() != null ? oldHeader.getRequired() : false;
      boolean requiredNew = newHeader.getRequired() != null ? newHeader.getRequired() : false;
      if (requiredOld && !requiredNew) {
        if (RESPONSE_HEADER_REQUIRED_DECREASED.enabled(context)) {
          return DiffResult.INCOMPATIBLE;
        }
      }
      if (!requiredOld && requiredNew) {
        // TODO: Document why desired or remove support. Client will just ignore new header?
        if (RESPONSE_HEADER_REQUIRED_INCREASED.enabled(context)) {
          return DiffResult.INCOMPATIBLE;
        }
      }
    }
    if (style) {
      return DiffResult.INCOMPATIBLE;
    }
    return DiffResult.COMPATIBLE;
  }

  public Header getOldHeader() {
    return this.oldHeader;
  }

  public Header getNewHeader() {
    return this.newHeader;
  }

  public DiffContext getContext() {
    return this.context;
  }

  public boolean isRequired() {
    return this.required;
  }

  public boolean isDeprecated() {
    return this.deprecated;
  }

  public boolean isStyle() {
    return this.style;
  }

  public boolean isExplode() {
    return this.explode;
  }

  public ChangedMetadata getDescription() {
    return this.description;
  }

  public ChangedExamples getExamples() {
    return this.examples;
  }

  public ChangedExample getExample() {
    return this.example;
  }

  public ChangedSchema getSchema() {
    return this.schema;
  }

  public ChangedContent getContent() {
    return this.content;
  }

  public ChangedExtensions getExtensions() {
    return this.extensions;
  }

  public ChangedHeader setRequired(final boolean required) {
    this.required = required;
    return this;
  }

  public ChangedHeader setDeprecated(final boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  public ChangedHeader setStyle(final boolean style) {
    this.style = style;
    return this;
  }

  public ChangedHeader setExplode(final boolean explode) {
    this.explode = explode;
    return this;
  }

  public ChangedHeader setDescription(final ChangedMetadata description) {
    this.description = description;
    return this;
  }

  public ChangedHeader setExamples(ChangedExamples examples) {
    this.examples = examples;
    return this;
  }

  public ChangedHeader setExample(ChangedExample example) {
    this.example = example;
    return this;
  }

  public ChangedHeader setSchema(final ChangedSchema schema) {
    this.schema = schema;
    return this;
  }

  public ChangedHeader setContent(final ChangedContent content) {
    this.content = content;
    return this;
  }

  public ChangedHeader setExtensions(final ChangedExtensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedHeader that = (ChangedHeader) o;
    return required == that.required
        && deprecated == that.deprecated
        && style == that.style
        && explode == that.explode
        && Objects.equals(oldHeader, that.oldHeader)
        && Objects.equals(newHeader, that.newHeader)
        && Objects.equals(context, that.context)
        && Objects.equals(description, that.description)
        && Objects.equals(examples, that.examples)
        && Objects.equals(example, that.example)
        && Objects.equals(schema, that.schema)
        && Objects.equals(content, that.content)
        && Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        oldHeader,
        newHeader,
        context,
        required,
        deprecated,
        style,
        explode,
        description,
        examples,
        example,
        schema,
        content,
        extensions);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedHeader(oldHeader="
        + this.getOldHeader()
        + ", newHeader="
        + this.getNewHeader()
        + ", context="
        + this.getContext()
        + ", required="
        + this.isRequired()
        + ", deprecated="
        + this.isDeprecated()
        + ", style="
        + this.isStyle()
        + ", explode="
        + this.isExplode()
        + ", description="
        + this.getDescription()
        + ", examples="
        + this.getExamples()
        + ", example="
        + this.getExample()
        + ", schema="
        + this.getSchema()
        + ", content="
        + this.getContent()
        + ", extensions="
        + this.getExtensions()
        + ")";
  }
}
