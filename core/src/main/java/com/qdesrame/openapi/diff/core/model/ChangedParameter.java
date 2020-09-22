package com.qdesrame.openapi.diff.core.model;

import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChangedParameter implements ComposedChanged {
  private final DiffContext context;
  private Parameter oldParameter;
  private Parameter newParameter;
  private String name;
  private String in;
  private boolean changeRequired;
  private boolean deprecated;
  private boolean changeStyle;
  private boolean changeExplode;
  private boolean changeAllowEmptyValue;
  private ChangedMetadata description;
  private ChangedSchema schema;
  private ChangedContent content;
  private ChangedExtensions extensions;

  public ChangedParameter(String name, String in, DiffContext context) {
    this.name = name;
    this.in = in;
    this.context = context;
  }

  @Override
  public List<Changed> getChangedElements() {
    return Arrays.asList(description, schema, content, extensions);
  }

  @Override
  public DiffResult isCoreChanged() {
    if (!changeRequired
        && !deprecated
        && !changeAllowEmptyValue
        && !changeStyle
        && !changeExplode) {
      return DiffResult.NO_CHANGES;
    }
    if ((!changeRequired || Boolean.TRUE.equals(oldParameter.getRequired()))
        && (!changeAllowEmptyValue || Boolean.TRUE.equals(newParameter.getAllowEmptyValue()))
        && !changeStyle
        && !changeExplode) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }

  public DiffContext getContext() {
    return this.context;
  }

  public Parameter getOldParameter() {
    return this.oldParameter;
  }

  public Parameter getNewParameter() {
    return this.newParameter;
  }

  public String getName() {
    return this.name;
  }

  public String getIn() {
    return this.in;
  }

  public boolean isChangeRequired() {
    return this.changeRequired;
  }

  public boolean isDeprecated() {
    return this.deprecated;
  }

  public boolean isChangeStyle() {
    return this.changeStyle;
  }

  public boolean isChangeExplode() {
    return this.changeExplode;
  }

  public boolean isChangeAllowEmptyValue() {
    return this.changeAllowEmptyValue;
  }

  public ChangedMetadata getDescription() {
    return this.description;
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

  public ChangedParameter setOldParameter(final Parameter oldParameter) {
    this.oldParameter = oldParameter;
    return this;
  }

  public ChangedParameter setNewParameter(final Parameter newParameter) {
    this.newParameter = newParameter;
    return this;
  }

  public ChangedParameter setName(final String name) {
    this.name = name;
    return this;
  }

  public ChangedParameter setIn(final String in) {
    this.in = in;
    return this;
  }

  public ChangedParameter setChangeRequired(final boolean changeRequired) {
    this.changeRequired = changeRequired;
    return this;
  }

  public ChangedParameter setDeprecated(final boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  public ChangedParameter setChangeStyle(final boolean changeStyle) {
    this.changeStyle = changeStyle;
    return this;
  }

  public ChangedParameter setChangeExplode(final boolean changeExplode) {
    this.changeExplode = changeExplode;
    return this;
  }

  public ChangedParameter setChangeAllowEmptyValue(final boolean changeAllowEmptyValue) {
    this.changeAllowEmptyValue = changeAllowEmptyValue;
    return this;
  }

  public ChangedParameter setDescription(final ChangedMetadata description) {
    this.description = description;
    return this;
  }

  public ChangedParameter setSchema(final ChangedSchema schema) {
    this.schema = schema;
    return this;
  }

  public ChangedParameter setContent(final ChangedContent content) {
    this.content = content;
    return this;
  }

  public ChangedParameter setExtensions(final ChangedExtensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedParameter that = (ChangedParameter) o;
    return changeRequired == that.changeRequired
        && deprecated == that.deprecated
        && changeStyle == that.changeStyle
        && changeExplode == that.changeExplode
        && changeAllowEmptyValue == that.changeAllowEmptyValue
        && Objects.equals(context, that.context)
        && Objects.equals(oldParameter, that.oldParameter)
        && Objects.equals(newParameter, that.newParameter)
        && Objects.equals(name, that.name)
        && Objects.equals(in, that.in)
        && Objects.equals(description, that.description)
        && Objects.equals(schema, that.schema)
        && Objects.equals(content, that.content)
        && Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        context,
        oldParameter,
        newParameter,
        name,
        in,
        changeRequired,
        deprecated,
        changeStyle,
        changeExplode,
        changeAllowEmptyValue,
        description,
        schema,
        content,
        extensions);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedParameter(context="
        + this.getContext()
        + ", oldParameter="
        + this.getOldParameter()
        + ", newParameter="
        + this.getNewParameter()
        + ", name="
        + this.getName()
        + ", in="
        + this.getIn()
        + ", changeRequired="
        + this.isChangeRequired()
        + ", deprecated="
        + this.isDeprecated()
        + ", changeStyle="
        + this.isChangeStyle()
        + ", changeExplode="
        + this.isChangeExplode()
        + ", changeAllowEmptyValue="
        + this.isChangeAllowEmptyValue()
        + ", description="
        + this.getDescription()
        + ", schema="
        + this.getSchema()
        + ", content="
        + this.getContent()
        + ", extensions="
        + this.getExtensions()
        + ")";
  }
}
