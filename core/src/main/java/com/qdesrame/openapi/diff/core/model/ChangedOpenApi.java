package com.qdesrame.openapi.diff.core.model;

import com.qdesrame.openapi.diff.core.utils.EndpointUtils;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangedOpenApi implements ComposedChanged {

  private OpenAPI oldSpecOpenApi;
  private OpenAPI newSpecOpenApi;

  private List<Endpoint> newEndpoints;
  private List<Endpoint> missingEndpoints;
  private List<ChangedOperation> changedOperations;
  private ChangedExtensions changedExtensions;

  public List<Endpoint> getDeprecatedEndpoints() {
    return changedOperations.stream()
        .filter(ChangedOperation::isDeprecated)
        .map(
            c ->
                EndpointUtils.convert2Endpoint(
                    c.getPathUrl(), c.getHttpMethod(), c.getNewOperation()))
        .collect(Collectors.toList());
  }

  @Override
  public List<Changed> getChangedElements() {
    return Stream.concat(changedOperations.stream(), Stream.of(changedExtensions))
        .collect(Collectors.toList());
  }

  @Override
  public DiffResult isCoreChanged() {
    if (newEndpoints.isEmpty() && missingEndpoints.isEmpty()) {
      return DiffResult.NO_CHANGES;
    }
    if (missingEndpoints.isEmpty()) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
