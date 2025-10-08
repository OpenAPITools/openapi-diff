package org.openapitools.openapidiff.core.model.schema;

import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.SCHEMA_PATTERN_CHANGED;

import java.util.Objects;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.DiffResult;

public class ChangedPattern implements Changed {
  private final String oldPattern;
  private final String newPattern;
  private final DiffContext context;

  public ChangedPattern(String oldPattern, String newPattern, DiffContext context) {
    this.oldPattern = oldPattern;
    this.newPattern = newPattern;
    this.context = context;
  }

  @Override
  public DiffResult isChanged() {
    if (Objects.equals(oldPattern, newPattern)) {
      return DiffResult.NO_CHANGES;
    } else if (SCHEMA_PATTERN_CHANGED.enabled(context)) {
      return DiffResult.INCOMPATIBLE;
    } else {
      return DiffResult.COMPATIBLE;
    }
  }

  @Override
  public boolean isCompatible() {
    return Objects.equals(oldPattern, newPattern);
  }

  public DiffResult isDiffBackwardCompatible() {
    if (!Objects.equals(oldPattern, newPattern)) {
      return DiffResult.INCOMPATIBLE;
    }
    return DiffResult.COMPATIBLE;
  }

  public String getOldPattern() {
    return oldPattern;
  }

  public String getNewPattern() {
    return newPattern;
  }

  public DiffContext getContext() {
    return context;
  }
}
