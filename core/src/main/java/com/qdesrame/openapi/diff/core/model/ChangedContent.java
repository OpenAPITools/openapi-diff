package com.qdesrame.openapi.diff.core.model;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import java.util.*;

public class ChangedContent implements ComposedChanged {
  private final Content oldContent;
  private final Content newContent;
  private final DiffContext context;
  private Map<String, MediaType> increased;
  private Map<String, MediaType> missing;
  private Map<String, ChangedMediaType> changed;

  public ChangedContent(Content oldContent, Content newContent, DiffContext context) {
    this.oldContent = oldContent;
    this.newContent = newContent;
    this.context = context;
    this.increased = new LinkedHashMap<>();
    this.missing = new LinkedHashMap<>();
    this.changed = new LinkedHashMap<>();
  }

  @Override
  public List<Changed> getChangedElements() {
    return new ArrayList<>(changed.values());
  }

  @Override
  public DiffResult isCoreChanged() {
    if (increased.isEmpty() && missing.isEmpty()) {
      return DiffResult.NO_CHANGES;
    }
    if (context.isRequest() && missing.isEmpty() || context.isResponse() && increased.isEmpty()) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }

  public Content getOldContent() {
    return this.oldContent;
  }

  public Content getNewContent() {
    return this.newContent;
  }

  public DiffContext getContext() {
    return this.context;
  }

  public Map<String, MediaType> getIncreased() {
    return this.increased;
  }

  public Map<String, MediaType> getMissing() {
    return this.missing;
  }

  public Map<String, ChangedMediaType> getChanged() {
    return this.changed;
  }

  public ChangedContent setIncreased(final Map<String, MediaType> increased) {
    this.increased = increased;
    return this;
  }

  public ChangedContent setMissing(final Map<String, MediaType> missing) {
    this.missing = missing;
    return this;
  }

  public ChangedContent setChanged(final Map<String, ChangedMediaType> changed) {
    this.changed = changed;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedContent that = (ChangedContent) o;
    return Objects.equals(oldContent, that.oldContent)
        && Objects.equals(newContent, that.newContent)
        && Objects.equals(context, that.context)
        && Objects.equals(increased, that.increased)
        && Objects.equals(missing, that.missing)
        && Objects.equals(changed, that.changed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oldContent, newContent, context, increased, missing, changed);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedContent(oldContent="
        + this.getOldContent()
        + ", newContent="
        + this.getNewContent()
        + ", context="
        + this.getContext()
        + ", increased="
        + this.getIncreased()
        + ", missing="
        + this.getMissing()
        + ", changed="
        + this.getChanged()
        + ")";
  }
}
