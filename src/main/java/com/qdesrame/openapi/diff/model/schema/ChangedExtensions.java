package com.qdesrame.openapi.diff.model.schema;

import com.qdesrame.openapi.diff.model.Changed;
import com.qdesrame.openapi.diff.model.ComposedChanged;
import com.qdesrame.openapi.diff.model.DiffContext;
import com.qdesrame.openapi.diff.model.DiffResult;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors
public class ChangedExtensions implements ComposedChanged {
  private final Map<String, Object> oldExtensions;
  private final Map<String, Object> newExtensions;
  private final DiffContext context;

  private Map<String, Changed> increased;
  private Map<String, Changed> missing;
  private Map<String, Changed> changed;

  public ChangedExtensions(
      Map<String, Object> oldExtensions, Map<String, Object> newExtensions, DiffContext context) {
    this.oldExtensions = oldExtensions;
    this.newExtensions = newExtensions;
    this.context = context;
    this.increased = new LinkedHashMap<>();
    this.missing = new LinkedHashMap<>();
    this.changed = new LinkedHashMap<>();
  }

  @Override
  public List<Changed> getChangedElements() {
    return Stream.of(increased, missing, changed)
        .map(Map::values)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  @Override
  public DiffResult isCoreChanged() {
    return DiffResult.NO_CHANGES;
  }
}
