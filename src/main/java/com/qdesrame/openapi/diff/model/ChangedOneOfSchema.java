package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
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
    if (context.isRequest() && missing.isEmpty() || context.isResponse() && increased.isEmpty()) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
