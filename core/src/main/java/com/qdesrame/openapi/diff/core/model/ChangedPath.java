package com.qdesrame.openapi.diff.core.model;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangedPath implements ComposedChanged {

  private final String pathUrl;
  private final PathItem oldPath;
  private final PathItem newPath;
  private final DiffContext context;

  Map<PathItem.HttpMethod, Operation> increased;
  Map<PathItem.HttpMethod, Operation> missing;
  List<ChangedOperation> changed;
  private ChangedExtensions extensions;

  public ChangedPath(String pathUrl, PathItem oldPath, PathItem newPath, DiffContext context) {
    this.pathUrl = pathUrl;
    this.oldPath = oldPath;
    this.newPath = newPath;
    this.context = context;
    this.increased = new LinkedHashMap<>();
    this.missing = new LinkedHashMap<>();
    this.changed = new ArrayList<>();
  }

  @Override
  public List<Changed> getChangedElements() {
    return Stream.concat(changed.stream(), Stream.of(extensions)).collect(Collectors.toList());
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
}
