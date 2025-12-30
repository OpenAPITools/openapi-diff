package org.openapitools.openapidiff.core.model;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.OPENAPI_ENDPOINTS_DECREASED;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.openapitools.openapidiff.core.compare.OpenApiDiffOptions;
import org.openapitools.openapidiff.core.utils.EndpointUtils;

public class ChangedOpenApi implements ComposedChanged {
  private final OpenApiDiffOptions options;
  @JsonIgnore private OpenAPI oldSpecOpenApi;
  @JsonIgnore private OpenAPI newSpecOpenApi;
  private List<Endpoint> newEndpoints;
  private List<Endpoint> missingEndpoints;
  private List<ChangedOperation> changedOperations;
  private List<ChangedSchema> changedSchemas;
  private ChangedExtensions changedExtensions;

  public ChangedOpenApi(OpenApiDiffOptions options) {
    this.options = options;
  }

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
    return Stream.concat(
            Stream.concat(changedOperations.stream(), Stream.of(changedExtensions)),
            changedSchemas.stream())
        .collect(Collectors.toList());
  }

  @Override
  public DiffResult isCoreChanged() {
    if (newEndpoints.isEmpty() && missingEndpoints.isEmpty()) {
      return DiffResult.NO_CHANGES;
    }
    if (!missingEndpoints.isEmpty()) {
      if (OPENAPI_ENDPOINTS_DECREASED.enabled(options.getConfig())) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    return DiffResult.COMPATIBLE;
  }

  public OpenAPI getOldSpecOpenApi() {
    return this.oldSpecOpenApi;
  }

  public OpenAPI getNewSpecOpenApi() {
    return this.newSpecOpenApi;
  }

  public List<Endpoint> getNewEndpoints() {
    return this.newEndpoints;
  }

  public List<Endpoint> getMissingEndpoints() {
    return this.missingEndpoints;
  }

  public List<ChangedOperation> getChangedOperations() {
    return this.changedOperations;
  }

  public ChangedExtensions getChangedExtensions() {
    return this.changedExtensions;
  }

  public List<ChangedSchema> getChangedSchemas() {
    return changedSchemas;
  }

  public ChangedOpenApi setOldSpecOpenApi(final OpenAPI oldSpecOpenApi) {
    this.oldSpecOpenApi = oldSpecOpenApi;
    return this;
  }

  public ChangedOpenApi setNewSpecOpenApi(final OpenAPI newSpecOpenApi) {
    this.newSpecOpenApi = newSpecOpenApi;
    return this;
  }

  public ChangedOpenApi setNewEndpoints(final List<Endpoint> newEndpoints) {
    this.newEndpoints = newEndpoints;
    return this;
  }

  public ChangedOpenApi setMissingEndpoints(final List<Endpoint> missingEndpoints) {
    this.missingEndpoints = missingEndpoints;
    return this;
  }

  public ChangedOpenApi setChangedOperations(final List<ChangedOperation> changedOperations) {
    this.changedOperations = changedOperations;
    return this;
  }

  public ChangedOpenApi setChangedExtensions(final ChangedExtensions changedExtensions) {
    this.changedExtensions = changedExtensions;
    return this;
  }

  public ChangedOpenApi setChangedSchemas(final List<ChangedSchema> changedSchemas) {
    this.changedSchemas = changedSchemas;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedOpenApi that = (ChangedOpenApi) o;
    return Objects.equals(oldSpecOpenApi, that.oldSpecOpenApi)
        && Objects.equals(newSpecOpenApi, that.newSpecOpenApi)
        && Objects.equals(newEndpoints, that.newEndpoints)
        && Objects.equals(missingEndpoints, that.missingEndpoints)
        && Objects.equals(changedOperations, that.changedOperations)
        && Objects.equals(changedExtensions, that.changedExtensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        oldSpecOpenApi,
        newSpecOpenApi,
        newEndpoints,
        missingEndpoints,
        changedOperations,
        changedExtensions);
  }

  @Override
  public String toString() {
    return "ChangedOpenApi(oldSpecOpenApi="
        + this.getOldSpecOpenApi()
        + ", newSpecOpenApi="
        + this.getNewSpecOpenApi()
        + ", newEndpoints="
        + this.getNewEndpoints()
        + ", missingEndpoints="
        + this.getMissingEndpoints()
        + ", changedOperations="
        + this.getChangedOperations()
        + ", changedExtensions="
        + this.getChangedExtensions()
        + ")";
  }
}
