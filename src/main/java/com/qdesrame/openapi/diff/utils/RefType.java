package com.qdesrame.openapi.diff.utils;

import lombok.Getter;

/** Created by adarsh.sharma on 07/01/18. */
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

  private String name;
}
