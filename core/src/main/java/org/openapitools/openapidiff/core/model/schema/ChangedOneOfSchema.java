package org.openapitools.openapidiff.core.model.schema;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.REQUEST_ONEOF_DECREASED;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.RESPONSE_ONEOF_INCREASED;

import io.swagger.v3.oas.models.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedSchema;
import org.openapitools.openapidiff.core.model.ComposedChanged;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.DiffResult;

public class ChangedOneOfSchema implements ComposedChanged {
  private final Map<String, String> oldMapping;
  private final Map<String, String> newMapping;
  private final DiffContext context;
  private Map<String, Schema> increased;
  private Map<String, Schema> missing;
  private Map<String, ChangedSchema> changed;

  public ChangedOneOfSchema(
      Map<String, String> oldMapping, Map<String, String> newMapping, DiffContext context) {
    this.oldMapping = oldMapping;
    this.newMapping = newMapping;
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
    if (context.isRequest() && !missing.isEmpty()) {
      if (REQUEST_ONEOF_DECREASED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    if (context.isResponse() && !increased.isEmpty()) {
      if (RESPONSE_ONEOF_INCREASED.enabled(context)) {
        return DiffResult.INCOMPATIBLE;
      }
    }
    return DiffResult.COMPATIBLE;
  }

  public Map<String, String> getOldMapping() {
    return this.oldMapping;
  }

  public Map<String, String> getNewMapping() {
    return this.newMapping;
  }

  public DiffContext getContext() {
    return this.context;
  }

  public Map<String, Schema> getIncreased() {
    return this.increased;
  }

  public Map<String, Schema> getMissing() {
    return this.missing;
  }

  public Map<String, ChangedSchema> getChanged() {
    return this.changed;
  }

  public ChangedOneOfSchema setIncreased(final Map<String, Schema> increased) {
    this.increased = increased;
    return this;
  }

  public ChangedOneOfSchema setMissing(final Map<String, Schema> missing) {
    this.missing = missing;
    return this;
  }

  public ChangedOneOfSchema setChanged(final Map<String, ChangedSchema> changed) {
    this.changed = changed;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedOneOfSchema that = (ChangedOneOfSchema) o;
    return Objects.equals(oldMapping, that.oldMapping)
        && Objects.equals(newMapping, that.newMapping)
        && Objects.equals(context, that.context)
        && Objects.equals(increased, that.increased)
        && Objects.equals(missing, that.missing)
        && Objects.equals(changed, that.changed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oldMapping, newMapping, context, increased, missing, changed);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedOneOfSchema(oldMapping="
        + this.getOldMapping()
        + ", newMapping="
        + this.getNewMapping()
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
