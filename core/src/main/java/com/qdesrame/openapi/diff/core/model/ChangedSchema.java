package com.qdesrame.openapi.diff.core.model;

import com.qdesrame.openapi.diff.core.model.schema.ChangedEnum;
import com.qdesrame.openapi.diff.core.model.schema.ChangedMaxLength;
import com.qdesrame.openapi.diff.core.model.schema.ChangedReadOnly;
import com.qdesrame.openapi.diff.core.model.schema.ChangedRequired;
import com.qdesrame.openapi.diff.core.model.schema.ChangedWriteOnly;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChangedSchema implements ComposedChanged {
  protected DiffContext context;
  protected Schema oldSchema;
  protected Schema newSchema;
  protected String type;
  protected Map<String, ChangedSchema> changedProperties;
  protected Map<String, Schema> increasedProperties;
  protected Map<String, Schema> missingProperties;
  protected boolean changeDeprecated;
  protected ChangedMetadata description;
  protected boolean changeTitle;
  protected ChangedRequired required;
  protected boolean changeDefault;
  protected ChangedEnum<?> enumeration;
  protected boolean changeFormat;
  protected ChangedReadOnly readOnly;
  protected ChangedWriteOnly writeOnly;
  protected boolean changedType;
  protected ChangedMaxLength maxLength;
  protected boolean discriminatorPropertyChanged;
  protected ChangedSchema items;
  protected ChangedOneOfSchema oneOfSchema;
  protected ChangedSchema addProp;
  private ChangedExtensions extensions;

  public ChangedSchema() {
    increasedProperties = new LinkedHashMap<>();
    missingProperties = new LinkedHashMap<>();
    changedProperties = new LinkedHashMap<>();
  }

  @Override
  public List<Changed> getChangedElements() {
    return Stream.concat(
            changedProperties.values().stream(),
            Stream.of(
                description,
                readOnly,
                writeOnly,
                items,
                oneOfSchema,
                addProp,
                enumeration,
                required,
                maxLength,
                extensions))
        .collect(Collectors.toList());
  }

  @Override
  public DiffResult isCoreChanged() {
    if (!changedType
        && (oldSchema == null && newSchema == null || oldSchema != null && newSchema != null)
        && !changeFormat
        && increasedProperties.size() == 0
        && missingProperties.size() == 0
        && changedProperties.values().size() == 0
        && !changeDeprecated
        && !discriminatorPropertyChanged) {
      return DiffResult.NO_CHANGES;
    }
    boolean compatibleForResponse =
        missingProperties.isEmpty() && (oldSchema == null || newSchema != null);
    if ((context.isRequest() && compatibleForRequest()
            || context.isResponse() && compatibleForResponse)
        && !changedType
        && !discriminatorPropertyChanged) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }

  private boolean compatibleForRequest() {
    if (PathItem.HttpMethod.PUT.equals(context.getMethod())) {
      if (increasedProperties.size() > 0) {
        return false;
      }
    }
    return (oldSchema != null || newSchema == null);
  }

  public DiffContext getContext() {
    return this.context;
  }

  public Schema getOldSchema() {
    return this.oldSchema;
  }

  public Schema getNewSchema() {
    return this.newSchema;
  }

  public String getType() {
    return this.type;
  }

  public Map<String, ChangedSchema> getChangedProperties() {
    return this.changedProperties;
  }

  public Map<String, Schema> getIncreasedProperties() {
    return this.increasedProperties;
  }

  public Map<String, Schema> getMissingProperties() {
    return this.missingProperties;
  }

  public boolean isChangeDeprecated() {
    return this.changeDeprecated;
  }

  public ChangedMetadata getDescription() {
    return this.description;
  }

  public boolean isChangeTitle() {
    return this.changeTitle;
  }

  public ChangedRequired getRequired() {
    return this.required;
  }

  public boolean isChangeDefault() {
    return this.changeDefault;
  }

  public ChangedEnum<?> getEnumeration() {
    return this.enumeration;
  }

  public boolean isChangeFormat() {
    return this.changeFormat;
  }

  public ChangedReadOnly getReadOnly() {
    return this.readOnly;
  }

  public ChangedWriteOnly getWriteOnly() {
    return this.writeOnly;
  }

  public boolean isChangedType() {
    return this.changedType;
  }

  public ChangedMaxLength getMaxLength() {
    return this.maxLength;
  }

  public boolean isDiscriminatorPropertyChanged() {
    return this.discriminatorPropertyChanged;
  }

  public ChangedSchema getItems() {
    return this.items;
  }

  public ChangedOneOfSchema getOneOfSchema() {
    return this.oneOfSchema;
  }

  public ChangedSchema getAddProp() {
    return this.addProp;
  }

  public ChangedExtensions getExtensions() {
    return this.extensions;
  }

  public ChangedSchema setContext(final DiffContext context) {
    this.context = context;
    return this;
  }

  public ChangedSchema setOldSchema(final Schema oldSchema) {
    this.oldSchema = oldSchema;
    return this;
  }

  public ChangedSchema setNewSchema(final Schema newSchema) {
    this.newSchema = newSchema;
    return this;
  }

  public ChangedSchema setType(final String type) {
    this.type = type;
    return this;
  }

  public ChangedSchema setChangedProperties(final Map<String, ChangedSchema> changedProperties) {
    this.changedProperties = changedProperties;
    return this;
  }

  public ChangedSchema setIncreasedProperties(final Map<String, Schema> increasedProperties) {
    this.increasedProperties = increasedProperties;
    return this;
  }

  public ChangedSchema setMissingProperties(final Map<String, Schema> missingProperties) {
    this.missingProperties = missingProperties;
    return this;
  }

  public ChangedSchema setChangeDeprecated(final boolean changeDeprecated) {
    this.changeDeprecated = changeDeprecated;
    return this;
  }

  public ChangedSchema setDescription(final ChangedMetadata description) {
    this.description = description;
    return this;
  }

  public ChangedSchema setChangeTitle(final boolean changeTitle) {
    this.changeTitle = changeTitle;
    return this;
  }

  public ChangedSchema setRequired(final ChangedRequired required) {
    this.required = required;
    return this;
  }

  public ChangedSchema setChangeDefault(final boolean changeDefault) {
    this.changeDefault = changeDefault;
    return this;
  }

  public ChangedSchema setEnumeration(final ChangedEnum<?> enumeration) {
    this.enumeration = enumeration;
    return this;
  }

  public ChangedSchema setChangeFormat(final boolean changeFormat) {
    this.changeFormat = changeFormat;
    return this;
  }

  public ChangedSchema setReadOnly(final ChangedReadOnly readOnly) {
    this.readOnly = readOnly;
    return this;
  }

  public ChangedSchema setWriteOnly(final ChangedWriteOnly writeOnly) {
    this.writeOnly = writeOnly;
    return this;
  }

  public ChangedSchema setChangedType(final boolean changedType) {
    this.changedType = changedType;
    return this;
  }

  public ChangedSchema setMaxLength(final ChangedMaxLength maxLength) {
    this.maxLength = maxLength;
    return this;
  }

  public ChangedSchema setDiscriminatorPropertyChanged(final boolean discriminatorPropertyChanged) {
    this.discriminatorPropertyChanged = discriminatorPropertyChanged;
    return this;
  }

  public ChangedSchema setItems(final ChangedSchema items) {
    this.items = items;
    return this;
  }

  public ChangedSchema setOneOfSchema(final ChangedOneOfSchema oneOfSchema) {
    this.oneOfSchema = oneOfSchema;
    return this;
  }

  public ChangedSchema setAddProp(final ChangedSchema addProp) {
    this.addProp = addProp;
    return this;
  }

  public ChangedSchema setExtensions(final ChangedExtensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedSchema that = (ChangedSchema) o;
    return changeDeprecated == that.changeDeprecated
        && changeTitle == that.changeTitle
        && changeDefault == that.changeDefault
        && changeFormat == that.changeFormat
        && changedType == that.changedType
        && discriminatorPropertyChanged == that.discriminatorPropertyChanged
        && Objects.equals(context, that.context)
        && Objects.equals(oldSchema, that.oldSchema)
        && Objects.equals(newSchema, that.newSchema)
        && Objects.equals(type, that.type)
        && Objects.equals(changedProperties, that.changedProperties)
        && Objects.equals(increasedProperties, that.increasedProperties)
        && Objects.equals(missingProperties, that.missingProperties)
        && Objects.equals(description, that.description)
        && Objects.equals(required, that.required)
        && Objects.equals(enumeration, that.enumeration)
        && Objects.equals(readOnly, that.readOnly)
        && Objects.equals(writeOnly, that.writeOnly)
        && Objects.equals(maxLength, that.maxLength)
        && Objects.equals(items, that.items)
        && Objects.equals(oneOfSchema, that.oneOfSchema)
        && Objects.equals(addProp, that.addProp)
        && Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        context,
        oldSchema,
        newSchema,
        type,
        changedProperties,
        increasedProperties,
        missingProperties,
        changeDeprecated,
        description,
        changeTitle,
        required,
        changeDefault,
        enumeration,
        changeFormat,
        readOnly,
        writeOnly,
        changedType,
        maxLength,
        discriminatorPropertyChanged,
        items,
        oneOfSchema,
        addProp,
        extensions);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedSchema(context="
        + this.getContext()
        + ", oldSchema="
        + this.getOldSchema()
        + ", newSchema="
        + this.getNewSchema()
        + ", type="
        + this.getType()
        + ", changedProperties="
        + this.getChangedProperties()
        + ", increasedProperties="
        + this.getIncreasedProperties()
        + ", missingProperties="
        + this.getMissingProperties()
        + ", changeDeprecated="
        + this.isChangeDeprecated()
        + ", description="
        + this.getDescription()
        + ", changeTitle="
        + this.isChangeTitle()
        + ", required="
        + this.getRequired()
        + ", changeDefault="
        + this.isChangeDefault()
        + ", enumeration="
        + this.getEnumeration()
        + ", changeFormat="
        + this.isChangeFormat()
        + ", readOnly="
        + this.getReadOnly()
        + ", writeOnly="
        + this.getWriteOnly()
        + ", changedType="
        + this.isChangedType()
        + ", maxLength="
        + this.getMaxLength()
        + ", discriminatorPropertyChanged="
        + this.isDiscriminatorPropertyChanged()
        + ", items="
        + this.getItems()
        + ", oneOfSchema="
        + this.getOneOfSchema()
        + ", addProp="
        + this.getAddProp()
        + ", extensions="
        + this.getExtensions()
        + ")";
  }
}
