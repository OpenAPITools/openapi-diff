package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.DiffContext;
import lombok.Value;

@Value
public class CacheKey {

  String left;
  String right;
  DiffContext context;
}
