package org.openapitools.openapidiff.core.model;

import io.swagger.v3.oas.models.parameters.RequestBody;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

  public RequestBody getOldRequestBody() {
    return this.oldRequestBody;
  }

  public RequestBody getNewRequestBody() {
    return this.newRequestBody;
  }

  public DiffContext getContext() {
    return this.context;
  }

  public boolean isChangeRequired() {
    return this.changeRequired;
  }

  public ChangedMetadata getDescription() {
    return this.description;
  }

  public ChangedContent getContent() {
    return this.content;
  }

  public ChangedExtensions getExtensions() {
    return this.extensions;
  }

  public ChangedRequestBody setChangeRequired(final boolean changeRequired) {
    this.changeRequired = changeRequired;
    return this;
  }

  public ChangedRequestBody setDescription(final ChangedMetadata description) {
    this.description = description;
    return this;
  }

  public ChangedRequestBody setContent(final ChangedContent content) {
    this.content = content;
    return this;
  }

  public ChangedRequestBody setExtensions(final ChangedExtensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedRequestBody that = (ChangedRequestBody) o;
    return changeRequired == that.changeRequired
        && Objects.equals(oldRequestBody, that.oldRequestBody)
        && Objects.equals(newRequestBody, that.newRequestBody)
        && Objects.equals(context, that.context)
        && Objects.equals(description, that.description)
        && Objects.equals(content, that.content)
        && Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        oldRequestBody, newRequestBody, context, changeRequired, description, content, extensions);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedRequestBody(oldRequestBody="
        + this.getOldRequestBody()
        + ", newRequestBody="
        + this.getNewRequestBody()
        + ", context="
        + this.getContext()
        + ", changeRequired="
        + this.isChangeRequired()
        + ", description="
        + this.getDescription()
        + ", content="
        + this.getContent()
        + ", extensions="
        + this.getExtensions()
        + ")";
  }
}
