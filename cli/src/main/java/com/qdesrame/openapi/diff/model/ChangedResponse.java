package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ChangedResponse implements ComposedChanged {
  private final ApiResponse oldApiResponse;
  private final ApiResponse newApiResponse;
  private final DiffContext context;

  private ChangedMetadata description;
  private ChangedHeaders headers;
  private ChangedContent content;
  private ChangedExtensions extensions;

  public ChangedResponse(
      ApiResponse oldApiResponse, ApiResponse newApiResponse, DiffContext context) {
    this.oldApiResponse = oldApiResponse;
    this.newApiResponse = newApiResponse;
    this.context = context;
  }

  @Override
  public List<Changed> getChangedElements() {
    return Arrays.asList(description, headers, content, extensions);
  }

  @Override
  public DiffResult isCoreChanged() {
    return DiffResult.NO_CHANGES;
  }
}
