package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.model.schema.*;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
      if (increasedProperties.size() > 0) return false;
    }

    return (oldSchema != null || newSchema == null);
  }
}
