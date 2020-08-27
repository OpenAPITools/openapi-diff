package com.qdesrame.openapi.diff.core.compare;

import com.qdesrame.openapi.diff.core.model.DiffContext;
import lombok.Value;

@Value
public class CacheKey {

  String left;
  String right;
  DiffContext context;
}
