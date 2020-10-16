package org.openapitools.openapidiff.core.model;

import io.swagger.v3.oas.models.headers.Header;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChangedHeaders implements ComposedChanged {
  private final Map<String, Header> oldHeaders;
  private final Map<String, Header> newHeaders;
  private final DiffContext context;
  private Map<String, Header> increased;
  private Map<String, Header> missing;
  private Map<String, ChangedHeader> changed;

  public ChangedHeaders(
      Map<String, Header> oldHeaders, Map<String, Header> newHeaders, DiffContext context) {
    this.oldHeaders = oldHeaders;
    this.newHeaders = newHeaders;
    this.context = context;
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
    if (missing.isEmpty()) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }

  public Map<String, Header> getOldHeaders() {
    return this.oldHeaders;
  }

  public Map<String, Header> getNewHeaders() {
    return this.newHeaders;
  }

  public DiffContext getContext() {
    return this.context;
  }

  public Map<String, Header> getIncreased() {
    return this.increased;
  }

  public Map<String, Header> getMissing() {
    return this.missing;
  }

  public Map<String, ChangedHeader> getChanged() {
    return this.changed;
  }

  public ChangedHeaders setIncreased(final Map<String, Header> increased) {
    this.increased = increased;
    return this;
  }

  public ChangedHeaders setMissing(final Map<String, Header> missing) {
    this.missing = missing;
    return this;
  }

  public ChangedHeaders setChanged(final Map<String, ChangedHeader> changed) {
    this.changed = changed;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedHeaders that = (ChangedHeaders) o;
    return Objects.equals(oldHeaders, that.oldHeaders)
        && Objects.equals(newHeaders, that.newHeaders)
        && Objects.equals(context, that.context)
        && Objects.equals(increased, that.increased)
        && Objects.equals(missing, that.missing)
        && Objects.equals(changed, that.changed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oldHeaders, newHeaders, context, increased, missing, changed);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedHeaders(oldHeaders="
        + this.getOldHeaders()
        + ", newHeaders="
        + this.getNewHeaders()
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
