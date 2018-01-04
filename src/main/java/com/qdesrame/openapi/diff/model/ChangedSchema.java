package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adarsh.sharma on 22/12/17.
 */
public class ChangedSchema implements RequestResponseChanged {
    protected Schema oldSchema;
    protected Schema newSchema;
    protected String type;
    protected Map<String, ChangedSchema> changedProperties;
    protected Map<String, Schema> increasedProperties;
    protected Map<String, Schema> missingProperties;
    protected boolean changeDeprecated;
    protected boolean changeDescription;
    protected boolean changeTitle;
    protected ListDiff<String> changeRequired;
    protected boolean changeDefault;
    protected ListDiff changeEnum;
    protected boolean changeFormat;
    protected boolean changeReadOnly;
    protected boolean changeWriteOnly;
    protected boolean changedType;
    protected boolean changedMaxLength;
    protected boolean discriminatorPropertyChanged;
    protected ChangedOneOfSchema changedOneOfSchema;

    public ChangedSchema() {
        increasedProperties = new HashMap<>();
        missingProperties = new HashMap<>();
        changedProperties = new HashMap<>();
    }

    @Override
    public boolean isDiff() {
        return Boolean.TRUE.equals(changedType)
                || changeWriteOnly
                || changedMaxLength
                || changeReadOnly
                || (changeEnum != null && (changeEnum.getIncreased().size() > 0 || changeEnum.getMissing().size() > 0))
                || changeFormat
                || increasedProperties.size() > 0
                || missingProperties.size() > 0
                || changedProperties.size() > 0
                || changeDeprecated
                || changeRequired.getIncreased().size() > 0
                || changeRequired.getMissing().size() > 0
                || discriminatorPropertyChanged
                || (changedOneOfSchema != null && changedOneOfSchema.isDiff());
    }

    @Override
    public boolean isDiffBackwardCompatible(boolean isRequest) {
        boolean backwardCompatibleForRequest = (changeEnum == null || changeEnum.getMissing().isEmpty()) &&
                (changeRequired == null || CollectionUtils.isEmpty(changeRequired.getIncreased())) &&
                (!changedMaxLength || newSchema.getMaxLength() == null ||
                        (oldSchema.getMaxLength() != null && oldSchema.getMaxLength()<= newSchema.getMaxLength()));

        boolean backwardCompatibleForResponse = (changeEnum == null || changeEnum.getIncreased().isEmpty()) &&
                missingProperties.isEmpty() &&
                (!changedMaxLength || oldSchema.getMaxLength() == null ||
                        (newSchema.getMaxLength() != null && newSchema.getMaxLength() <= oldSchema.getMaxLength()));

        return (isRequest && backwardCompatibleForRequest || !isRequest && backwardCompatibleForResponse )
                && !changedType
                && !discriminatorPropertyChanged
                && (changedOneOfSchema == null || changedOneOfSchema.isDiffBackwardCompatible(isRequest))
                && changedProperties.values().stream().allMatch(p -> p.isDiffBackwardCompatible(isRequest));
    }

    public Schema getOldSchema() {
        return oldSchema;
    }

    public Schema getNewSchema() {
        return newSchema;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, ChangedSchema> getChangedProperties() {
        return changedProperties;
    }

    public void setChangedProperties(Map<String, ChangedSchema> changedProperties) {
        this.changedProperties = changedProperties;
    }

    public Map<String, Schema> getIncreasedProperties() {
        return increasedProperties;
    }

    public void setIncreasedProperties(Map<String, Schema> increasedProperties) {
        this.increasedProperties = increasedProperties;
    }

    public Map<String, Schema> getMissingProperties() {
        return missingProperties;
    }

    public void setMissingProperties(Map<String, Schema> missingProperties) {
        this.missingProperties = missingProperties;
    }

    public boolean isChangeDeprecated() {
        return changeDeprecated;
    }

    public void setChangeDeprecated(boolean changeDeprecated) {
        this.changeDeprecated = changeDeprecated;
    }

    public boolean isChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(boolean changeDescription) {
        this.changeDescription = changeDescription;
    }

    public boolean isChangeTitle() {
        return changeTitle;
    }

    public void setChangeTitle(boolean changeTitle) {
        this.changeTitle = changeTitle;
    }

    public ListDiff<String> getChangeRequired() {
        return changeRequired;
    }

    public void setChangeRequired(ListDiff<String> changeRequired) {
        this.changeRequired = changeRequired;
    }

    public boolean isChangeDefault() {
        return changeDefault;
    }

    public void setChangeDefault(boolean changeDefault) {
        this.changeDefault = changeDefault;
    }

    public ListDiff getChangeEnum() {
        return changeEnum;
    }

    public void setChangeEnum(ListDiff changeEnum) {
        this.changeEnum = changeEnum;
    }

    public boolean isChangeFormat() {
        return changeFormat;
    }

    public void setChangeFormat(boolean changeFormat) {
        this.changeFormat = changeFormat;
    }

    public boolean isChangeReadOnly() {
        return changeReadOnly;
    }

    public void setChangeReadOnly(boolean changeReadOnly) {
        this.changeReadOnly = changeReadOnly;
    }

    public boolean isChangeWriteOnly() {
        return changeWriteOnly;
    }

    public void setChangeWriteOnly(boolean changeWriteOnly) {
        this.changeWriteOnly = changeWriteOnly;
    }

    public boolean isChangedType() {
        return changedType;
    }

    public void setChangedType(boolean changedType) {
        this.changedType = changedType;
    }

    public boolean isChangedMaxLength() {
        return changedMaxLength;
    }

    public void setChangedMaxLength(boolean changedMaxLength) {
        this.changedMaxLength = changedMaxLength;
    }

    public boolean isDiscriminatorPropertyChanged() {
        return discriminatorPropertyChanged;
    }

    public void setDiscriminatorPropertyChanged(boolean discriminatorPropertyChanged) {
        this.discriminatorPropertyChanged = discriminatorPropertyChanged;
    }

    public ChangedOneOfSchema getChangedOneOfSchema() {
        return changedOneOfSchema;
    }

    public void setChangedOneOfSchema(ChangedOneOfSchema changedOneOfSchema) {
        this.changedOneOfSchema = changedOneOfSchema;
    }

    public ChangedSchema setOldSchema(Schema oldSchema) {
        this.oldSchema = oldSchema;
        return this;
    }

    public ChangedSchema setNewSchema(Schema newSchema) {
        this.newSchema = newSchema;
        return this;
    }
}
