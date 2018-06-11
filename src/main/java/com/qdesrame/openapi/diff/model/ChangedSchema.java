package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.model.schema.ChangedExtensions;
import com.qdesrame.openapi.diff.model.schema.ChangedReadOnly;
import com.qdesrame.openapi.diff.model.schema.ChangedWriteOnly;
import com.qdesrame.openapi.diff.utils.ChangedUtils;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by adarsh.sharma on 22/12/17.
 */
@Getter
@Setter
public class ChangedSchema implements Changed {
    protected DiffContext context;
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
    protected ChangedReadOnly changedReadOnly;
    protected ChangedWriteOnly changedWriteOnly;
    protected boolean changedType;
    protected boolean changedMaxLength;
    protected boolean discriminatorPropertyChanged;
    protected ChangedSchema changedItems;
    protected ChangedOneOfSchema changedOneOfSchema;
    protected ChangedSchema addPropChangedSchema;
    protected ChangedExtensions changedExtensions;

    public ChangedSchema() {
        increasedProperties = new LinkedHashMap<>();
        missingProperties = new LinkedHashMap<>();
        changedProperties = new LinkedHashMap<>();
    }

    @Override
    public DiffResult isChanged() {
        if (!changedType && (oldSchema == null && newSchema == null || oldSchema != null && newSchema != null)
                && ChangedUtils.isUnchanged(changedWriteOnly) && ChangedUtils.isUnchanged(changedReadOnly)
                && !changedMaxLength && (changeEnum == null || changeEnum.isUnchanged())
                && !changeFormat && increasedProperties.size() == 0 && missingProperties.size() == 0
                && changedProperties.values().size() == 0 && !changeDeprecated
                && (changeRequired == null || changeRequired.isUnchanged()) && !discriminatorPropertyChanged
                && ChangedUtils.isUnchanged(addPropChangedSchema) && ChangedUtils.isUnchanged(changedOneOfSchema)
                && ChangedUtils.isUnchanged(changedItems) && ChangedUtils.isUnchanged(changedExtensions)) {
            return DiffResult.NO_CHANGES;
        }
        boolean backwardCompatibleForRequest = (changeEnum == null || changeEnum.getMissing().isEmpty()) &&
                (changeRequired == null || CollectionUtils.isEmpty(changeRequired.getIncreased())) &&
                (oldSchema != null || newSchema == null) &&
                (!changedMaxLength || newSchema.getMaxLength() == null ||
                        (oldSchema.getMaxLength() != null && oldSchema.getMaxLength() <= newSchema.getMaxLength()));

        boolean backwardCompatibleForResponse = (changeEnum == null || changeEnum.getIncreased().isEmpty()) &&
                (changeRequired == null || CollectionUtils.isEmpty(changeRequired.getMissing())) &&
                missingProperties.isEmpty() &&
                (oldSchema == null || newSchema != null) &&
                (!changedMaxLength || oldSchema.getMaxLength() == null ||
                        (newSchema.getMaxLength() != null && newSchema.getMaxLength() <= oldSchema.getMaxLength()));

        if ((context.isRequest() && backwardCompatibleForRequest || context.isResponse() && backwardCompatibleForResponse)
                && !changedType && !discriminatorPropertyChanged && ChangedUtils.isCompatible(changedOneOfSchema)
                && ChangedUtils.isCompatible(addPropChangedSchema)
                && changedProperties.values().stream().allMatch(Changed::isCompatible)
                && ChangedUtils.isCompatible(changedItems) && ChangedUtils.isCompatible(changedExtensions)) {
            return DiffResult.COMPATIBLE;
        }

        return DiffResult.INCOMPATIBLE;
    }
}
