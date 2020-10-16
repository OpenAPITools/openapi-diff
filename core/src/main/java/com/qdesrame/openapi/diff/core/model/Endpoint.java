package com.qdesrame.openapi.diff.core.model;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import java.util.Objects;

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

  public Endpoint() {}

  public String getPathUrl() {
    return this.pathUrl;
  }

  public PathItem.HttpMethod getMethod() {
    return this.method;
  }

  public String getSummary() {
    return this.summary;
  }

  public PathItem getPath() {
    return this.path;
  }

  public Operation getOperation() {
    return this.operation;
  }

  public void setPathUrl(final String pathUrl) {
    this.pathUrl = pathUrl;
  }

  public void setMethod(final PathItem.HttpMethod method) {
    this.method = method;
  }

  public void setSummary(final String summary) {
    this.summary = summary;
  }

  public void setPath(final PathItem path) {
    this.path = path;
  }

  public void setOperation(final Operation operation) {
    this.operation = operation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Endpoint endpoint = (Endpoint) o;
    return Objects.equals(pathUrl, endpoint.pathUrl)
        && method == endpoint.method
        && Objects.equals(summary, endpoint.summary)
        && Objects.equals(path, endpoint.path)
        && Objects.equals(operation, endpoint.operation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pathUrl, method, summary, path, operation);
  }
}
