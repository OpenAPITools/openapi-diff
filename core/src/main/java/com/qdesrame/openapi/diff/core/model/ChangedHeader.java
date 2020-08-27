package com.qdesrame.openapi.diff.core.model;

import io.swagger.v3.oas.models.headers.Header;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangedHeader implements ComposedChanged {

  private final Header oldHeader;
  private final Header newHeader;
  private final DiffContext context;

  private boolean required;
  private boolean deprecated;
  private boolean style;
  private boolean explode;
  private ChangedMetadata description;
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
    return Arrays.asList(description, schema, content, extensions);
  }

  @Override
  public DiffResult isCoreChanged() {
    if (!required && !deprecated && !style && !explode) {
      return DiffResult.NO_CHANGES;
    }
    if (!required && !style && !explode) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
