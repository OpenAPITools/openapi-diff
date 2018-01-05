package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adarsh.sharma on 22/12/17.
 */
@Getter
@Setter
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
                || (changeRequired != null && changeRequired.getIncreased().size() > 0)
                || (changeRequired != null && changeRequired.getMissing().size() > 0)
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
}
