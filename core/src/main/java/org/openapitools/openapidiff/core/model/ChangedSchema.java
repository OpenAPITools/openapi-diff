package org.openapitools.openapidiff.core.model;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_REQUIRED_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SCHEMA_DISCRIMINATOR_CHANGED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SCHEMA_TYPE_CHANGED;

import io.swagger.v3.oas.models.media.Schema;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.openapitools.openapidiff.core.model.schema.ChangedEnum;
import org.openapitools.openapidiff.core.model.schema.ChangedMaxLength;
import org.openapitools.openapidiff.core.model.schema.ChangedNumericRange;
import org.openapitools.openapidiff.core.model.schema.ChangedReadOnly;
import org.openapitools.openapidiff.core.model.schema.ChangedRequired;
import org.openapitools.openapidiff.core.model.schema.ChangedWriteOnly;

public class ChangedSchema implements ComposedChanged {
  protected DiffContext context;
  protected Schema<?> oldSchema;
  protected Schema<?> newSchema;
  protected String type;
  protected Map<String, ChangedSchema> changedProperties;
  protected Map<String, Schema<?>> increasedProperties;
  protected Map<String, Schema<?>> missingProperties;
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
  protected ChangedNumericRange numericRange;
  protected boolean discriminatorPropertyChanged;
  protected ChangedSchema items;
  protected ChangedOneOfSchema oneOfSchema;
  protected ChangedSchema addProp;
  private ChangedExtensions extensions;

  // Flags to avoid recursive calls to isChanged() and getChangedElements()
  private boolean gettingChangedElements = false;
  private boolean gettingIsChanged = false;

  // cached results for isChanged()
  private DiffResult changed;
  private DiffResult coreChanged;

  // cached results for getChangedElements()
  private List<Changed> changedElements;

  public ChangedSchema() {
    increasedProperties = new LinkedHashMap<>();
    missingProperties = new LinkedHashMap<>();
    changedProperties = new LinkedHashMap<>();
  }

  private void clearChangedCache() {
    this.changed = null;
    this.coreChanged = null;
    this.changedElements = null;
  }

  @Override
  public DiffResult isChanged() {
    if (gettingIsChanged) {
      return DiffResult.NO_CHANGES;
    }

    gettingIsChanged = true;
    if (this.changed == null) {
      DiffResult elementsResult =
          DiffResult.fromWeight(
              getChangedElements().stream()
                  .filter(Objects::nonNull)
                  .map(Changed::isChanged)
                  .mapToInt(DiffResult::getWeight)
                  .max()
                  .orElse(0));
      DiffResult result;
      if (isCoreChanged().getWeight() > elementsResult.getWeight()) {
        result = isCoreChanged();
      } else {
        result = elementsResult;
      }
      this.changed = result;
    }
    gettingIsChanged = false;

    return this.changed;
  }

  @Override
  public List<Changed> getChangedElements() {
    if (gettingChangedElements) {
      return Collections.emptyList();
    }

    gettingChangedElements = true;
    if (changedElements == null) {
      this.changedElements =
          Stream.concat(
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
                      numericRange,
                      extensions))
              .collect(Collectors.toList());
    }
    gettingChangedElements = false;

    return this.changedElements;
  }

  @Override
  public DiffResult isCoreChanged() {
    if (this.coreChanged == null) {
      this.coreChanged = calculateCoreChanged();
    }

    return this.coreChanged;
  }

  private DiffResult calculateCoreChanged() {
    if (!changedType
        && (oldSchema == null && newSchema == null || oldSchema != null && newSchema != null)
        && !changeFormat
        && increasedProperties.isEmpty()
        && missingProperties.isEmpty()
        && changedProperties.values().isEmpty()
        && !changeDeprecated
        && !discriminatorPropertyChanged) {
      return DiffResult.NO_CHANGES;
    }
    if (changedType) {
      if (SCHEMA_TYPE_CHANGED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    if (discriminatorPropertyChanged) {
      if (SCHEMA_DISCRIMINATOR_CHANGED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }

    if (!compatibleForRequest() || !compatibleForResponse()) {
      return DiffResult.INCOMPATIBLE;
    }
    return DiffResult.COMPATIBLE;
  }

  private boolean compatibleForRequest() {
    if (context.isRequest()) {
      if (oldSchema == null && newSchema != null) {
        // TODO: dead code? If not, create test.
        return false;
      }
    }
    return true;
  }

  private boolean compatibleForResponse() {
    if (context.isResponse()) {
      if (oldSchema != null) {
        if (newSchema == null) {
          // TODO: dead code? If not, create test.
          return false;
        }
        if (oldSchema.getRequired() != null
            && missingProperties.keySet().stream()
                .anyMatch(prop -> oldSchema.getRequired().contains(prop))) {
          if (RESPONSE_REQUIRED_DECREASED.enabled(context)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  public DiffContext getContext() {
    return this.context;
  }

  public Schema<?> getOldSchema() {
    return this.oldSchema;
  }

  public Schema<?> getNewSchema() {
    return this.newSchema;
  }

  public String getType() {
    return this.type;
  }

  public Map<String, ChangedSchema> getChangedProperties() {
    return this.changedProperties;
  }

  public Map<String, Schema<?>> getIncreasedProperties() {
    return this.increasedProperties;
  }

  public Map<String, Schema<?>> getMissingProperties() {
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

  public ChangedSchema setOldSchema(final Schema<?> oldSchema) {
    this.oldSchema = oldSchema;
    return this;
  }

  public ChangedSchema setNewSchema(final Schema<?> newSchema) {
    this.newSchema = newSchema;
    return this;
  }

  public ChangedSchema setType(final String type) {
    this.type = type;
    return this;
  }

  public ChangedSchema setChangedProperties(final Map<String, ChangedSchema> changedProperties) {
    clearChangedCache();
    this.changedProperties = changedProperties;
    return this;
  }

  public ChangedSchema setIncreasedProperties(final Map<String, Schema<?>> increasedProperties) {
    clearChangedCache();
    this.increasedProperties = increasedProperties;
    return this;
  }

  public ChangedSchema setMissingProperties(final Map<String, Schema<?>> missingProperties) {
    clearChangedCache();
    this.missingProperties = missingProperties;
    return this;
  }

  public ChangedSchema setChangeDeprecated(final boolean changeDeprecated) {
    clearChangedCache();
    this.changeDeprecated = changeDeprecated;
    return this;
  }

  public ChangedSchema setDescription(final ChangedMetadata description) {
    clearChangedCache();
    this.description = description;
    return this;
  }

  public ChangedSchema setChangeTitle(final boolean changeTitle) {
    clearChangedCache();
    this.changeTitle = changeTitle;
    return this;
  }

  public ChangedSchema setRequired(final ChangedRequired required) {
    clearChangedCache();
    this.required = required;
    return this;
  }

  public ChangedSchema setChangeDefault(final boolean changeDefault) {
    clearChangedCache();
    this.changeDefault = changeDefault;
    return this;
  }

  public ChangedSchema setEnumeration(final ChangedEnum<?> enumeration) {
    clearChangedCache();
    this.enumeration = enumeration;
    return this;
  }

  public ChangedSchema setChangeFormat(final boolean changeFormat) {
    clearChangedCache();
    this.changeFormat = changeFormat;
    return this;
  }

  public ChangedSchema setReadOnly(final ChangedReadOnly readOnly) {
    clearChangedCache();
    this.readOnly = readOnly;
    return this;
  }

  public ChangedSchema setWriteOnly(final ChangedWriteOnly writeOnly) {
    clearChangedCache();
    this.writeOnly = writeOnly;
    return this;
  }

  public ChangedSchema setChangedType(final boolean changedType) {
    clearChangedCache();
    this.changedType = changedType;
    return this;
  }

  public ChangedSchema setMaxLength(final ChangedMaxLength maxLength) {
    clearChangedCache();
    this.maxLength = maxLength;
    return this;
  }

  public ChangedSchema setNumericRange(final ChangedNumericRange numericRange) {
    clearChangedCache();
    this.numericRange = numericRange;
    return this;
  }

  public ChangedSchema setDiscriminatorPropertyChanged(final boolean discriminatorPropertyChanged) {
    clearChangedCache();
    this.discriminatorPropertyChanged = discriminatorPropertyChanged;
    return this;
  }

  public ChangedSchema setItems(final ChangedSchema items) {
    clearChangedCache();
    this.items = items;
    return this;
  }

  public ChangedSchema setOneOfSchema(final ChangedOneOfSchema oneOfSchema) {
    clearChangedCache();
    this.oneOfSchema = oneOfSchema;
    return this;
  }

  public ChangedSchema setAddProp(final ChangedSchema addProp) {
    clearChangedCache();
    this.addProp = addProp;
    return this;
  }

  public ChangedSchema setExtensions(final ChangedExtensions extensions) {
    clearChangedCache();
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
        && Objects.equals(numericRange, that.numericRange)
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
        numericRange,
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
