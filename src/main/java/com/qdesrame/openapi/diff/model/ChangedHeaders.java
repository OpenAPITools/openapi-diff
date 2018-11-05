package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.headers.Header;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/** Created by adarsh.sharma on 28/12/17. */
@Getter
@Setter
@Accessors(chain = true)
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
}
