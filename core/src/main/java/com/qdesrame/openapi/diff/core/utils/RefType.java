package com.qdesrame.openapi.diff.core.utils;

public enum RefType {
  REQUEST_BODIES("requestBodies"),
  RESPONSES("responses"),
  PARAMETERS("parameters"),
  SCHEMAS("schemas"),
  HEADERS("headers"),
  SECURITY_SCHEMES("securitySchemes");

  RefType(String name) {
    this.name = name;
  }

  private final String name;

  public String getName() {
    return this.name;
  }
}
