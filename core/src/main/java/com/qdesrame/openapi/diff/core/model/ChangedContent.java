package com.qdesrame.openapi.diff.core.model;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
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
}
