package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.model.schema.ChangedExtensions;
import com.qdesrame.openapi.diff.model.schema.ChangedReadOnly;
import com.qdesrame.openapi.diff.model.schema.ChangedWriteOnly;
import io.swagger.v3.oas.models.media.Schema;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;

/** Created by adarsh.sharma on 22/12/17. */
@Getter
@Setter
@Accessors(chain = true)
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
  protected ListDiff<String> changeRequired;
  protected boolean changeDefault;
  protected ListDiff changeEnum;
  protected boolean changeFormat;
  protected ChangedReadOnly readOnly;
  protected ChangedWriteOnly writeOnly;
  protected boolean changedType;
  protected boolean changedMaxLength;
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
            Stream.of(description, readOnly, writeOnly, items, oneOfSchema, addProp, extensions))
        .collect(Collectors.toList());
  }

  @Override
  public DiffResult isCoreChanged() {
    if (!changedType
        && (oldSchema == null && newSchema == null || oldSchema != null && newSchema != null)
        && !changedMaxLength
        && (changeEnum == null || changeEnum.isUnchanged())
        && !changeFormat
        && increasedProperties.size() == 0
        && missingProperties.size() == 0
        && changedProperties.values().size() == 0
        && !changeDeprecated
        && (changeRequired == null || changeRequired.isUnchanged())
        && !discriminatorPropertyChanged) {
      return DiffResult.NO_CHANGES;
    }
    boolean compatibleForRequest =
        (changeEnum == null || changeEnum.getMissing().isEmpty())
            && (changeRequired == null || CollectionUtils.isEmpty(changeRequired.getIncreased()))
            && (oldSchema != null || newSchema == null)
            && (!changedMaxLength
                || newSchema.getMaxLength() == null
                || (oldSchema.getMaxLength() != null
                    && oldSchema.getMaxLength() <= newSchema.getMaxLength()));

    boolean compatibleForResponse =
        (changeEnum == null || changeEnum.getIncreased().isEmpty())
            && (changeRequired == null || CollectionUtils.isEmpty(changeRequired.getMissing()))
            && missingProperties.isEmpty()
            && (oldSchema == null || newSchema != null)
            && (!changedMaxLength
                || oldSchema.getMaxLength() == null
                || (newSchema.getMaxLength() != null
                    && newSchema.getMaxLength() <= oldSchema.getMaxLength()));
    if ((context.isRequest() && compatibleForRequest
            || context.isResponse() && compatibleForResponse)
        && !changedType
        && !discriminatorPropertyChanged) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
