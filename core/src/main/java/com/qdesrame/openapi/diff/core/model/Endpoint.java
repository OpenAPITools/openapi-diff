package com.qdesrame.openapi.diff.core.model;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import lombok.Data;

@Data
public class Endpoint {

  private String pathUrl;
  private PathItem.HttpMethod method;
  private String summary;

  private PathItem path;
  private Operation operation;

  @Override
  public String toString() {
    return method + " " + pathUrl;
  }
}
