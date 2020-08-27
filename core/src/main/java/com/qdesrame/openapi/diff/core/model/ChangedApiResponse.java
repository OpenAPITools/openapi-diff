package com.qdesrame.openapi.diff.core.model;

import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangedApiResponse implements ComposedChanged {

  private final ApiResponses oldApiResponses;
  private final ApiResponses newApiResponses;
  private final DiffContext context;

  private Map<String, ApiResponse> increased;
  private Map<String, ApiResponse> missing;
  private Map<String, ChangedResponse> changed;
  private ChangedExtensions extensions;

  public ChangedApiResponse(
      ApiResponses oldApiResponses, ApiResponses newApiResponses, DiffContext context) {
    this.oldApiResponses = oldApiResponses;
    this.newApiResponses = newApiResponses;
    this.context = context;
    this.missing = new LinkedHashMap<>();
    this.increased = new LinkedHashMap<>();
    this.changed = new LinkedHashMap<>();
  }

  @Override
  public List<Changed> getChangedElements() {
    return Stream.concat(changed.values().stream(), Stream.of(extensions))
        .collect(Collectors.toList());
  }

  @Override
  public DiffResult isCoreChanged() {
    if (increased.isEmpty() && missing.isEmpty()) {
      return DiffResult.NO_CHANGES;
    }
    if (!increased.isEmpty() && missing.isEmpty()) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
