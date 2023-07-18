package org.openapitools.openapidiff.core.model;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_RESPONSES_DECREASED;

import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    if (!missing.isEmpty()) {
      if (RESPONSE_RESPONSES_DECREASED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    return DiffResult.COMPATIBLE;
  }

  public ApiResponses getOldApiResponses() {
    return this.oldApiResponses;
  }

  public ApiResponses getNewApiResponses() {
    return this.newApiResponses;
  }

  public DiffContext getContext() {
    return this.context;
  }

  public Map<String, ApiResponse> getIncreased() {
    return this.increased;
  }

  public Map<String, ApiResponse> getMissing() {
    return this.missing;
  }

  public Map<String, ChangedResponse> getChanged() {
    return this.changed;
  }

  public ChangedExtensions getExtensions() {
    return this.extensions;
  }

  public ChangedApiResponse setIncreased(final Map<String, ApiResponse> increased) {
    this.increased = increased;
    return this;
  }

  public ChangedApiResponse setMissing(final Map<String, ApiResponse> missing) {
    this.missing = missing;
    return this;
  }

  public ChangedApiResponse setChanged(final Map<String, ChangedResponse> changed) {
    this.changed = changed;
    return this;
  }

  public ChangedApiResponse setExtensions(final ChangedExtensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedApiResponse that = (ChangedApiResponse) o;
    return Objects.equals(oldApiResponses, that.oldApiResponses)
        && Objects.equals(newApiResponses, that.newApiResponses)
        && Objects.equals(context, that.context)
        && Objects.equals(increased, that.increased)
        && Objects.equals(missing, that.missing)
        && Objects.equals(changed, that.changed)
        && Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        oldApiResponses, newApiResponses, context, increased, missing, changed, extensions);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedApiResponse(oldApiResponses="
        + this.getOldApiResponses()
        + ", newApiResponses="
        + this.getNewApiResponses()
        + ", context="
        + this.getContext()
        + ", increased="
        + this.getIncreased()
        + ", missing="
        + this.getMissing()
        + ", changed="
        + this.getChanged()
        + ", extensions="
        + this.getExtensions()
        + ")";
  }
}
