package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.parameters.RequestBody;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangedRequestBody implements ComposedChanged {

  private final RequestBody oldRequestBody;
  private final RequestBody newRequestBody;
  private final DiffContext context;

  private boolean changeRequired;
  private ChangedMetadata description;
  private ChangedContent content;
  private ChangedExtensions extensions;

  public ChangedRequestBody(
      RequestBody oldRequestBody, RequestBody newRequestBody, DiffContext context) {
    this.oldRequestBody = oldRequestBody;
    this.newRequestBody = newRequestBody;
    this.context = context;
  }

  @Override
  public List<Changed> getChangedElements() {
    return Arrays.asList(description, content, extensions);
  }

  @Override
  public DiffResult isCoreChanged() {
    if (!changeRequired) {
      return DiffResult.NO_CHANGES;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
