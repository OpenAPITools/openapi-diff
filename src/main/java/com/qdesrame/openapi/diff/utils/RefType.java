package com.qdesrame.openapi.diff.utils;

import lombok.Getter;

@Getter
public enum RefType {
  REQUEST_BODIES("requestBodies"),
  RESPONSES("responses"),
  PARAMETERS("parameters"),
  SCHEMAS("schemas"),
  HEADERS("headers"),
  SECURITY_SCHEMES("securitySchemes"),
  ;

  RefType(String name) {
    this.name = name;
  }

  private final String name;
}
