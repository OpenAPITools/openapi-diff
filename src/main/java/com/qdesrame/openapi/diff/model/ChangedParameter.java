package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
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
}
