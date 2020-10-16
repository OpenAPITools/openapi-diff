package com.qdesrame.openapi.diff.core.model;

import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

  public ApiResponse getOldApiResponse() {
    return this.oldApiResponse;
  }

  public ApiResponse getNewApiResponse() {
    return this.newApiResponse;
  }

  public DiffContext getContext() {
    return this.context;
  }

  public ChangedMetadata getDescription() {
    return this.description;
  }

  public ChangedHeaders getHeaders() {
    return this.headers;
  }

  public ChangedContent getContent() {
    return this.content;
  }

  public ChangedExtensions getExtensions() {
    return this.extensions;
  }

  public ChangedResponse setDescription(final ChangedMetadata description) {
    this.description = description;
    return this;
  }

  public ChangedResponse setHeaders(final ChangedHeaders headers) {
    this.headers = headers;
    return this;
  }

  public ChangedResponse setContent(final ChangedContent content) {
    this.content = content;
    return this;
  }

  public ChangedResponse setExtensions(final ChangedExtensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedResponse that = (ChangedResponse) o;
    return Objects.equals(oldApiResponse, that.oldApiResponse)
        && Objects.equals(newApiResponse, that.newApiResponse)
        && Objects.equals(context, that.context)
        && Objects.equals(description, that.description)
        && Objects.equals(headers, that.headers)
        && Objects.equals(content, that.content)
        && Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        oldApiResponse, newApiResponse, context, description, headers, content, extensions);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedResponse(oldApiResponse="
        + this.getOldApiResponse()
        + ", newApiResponse="
        + this.getNewApiResponse()
        + ", context="
        + this.getContext()
        + ", description="
        + this.getDescription()
        + ", headers="
        + this.getHeaders()
        + ", content="
        + this.getContent()
        + ", extensions="
        + this.getExtensions()
        + ")";
  }
}
