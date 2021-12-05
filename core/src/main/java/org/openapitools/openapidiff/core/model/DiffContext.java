package org.openapitools.openapidiff.core.model;

import io.swagger.v3.oas.models.PathItem;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DiffContext {

  private String url;
  private Map<String, String> parameters;
  private PathItem.HttpMethod method;
  private boolean response;
  private boolean request;
  private Boolean required;

  public DiffContext() {
    parameters = new HashMap<>();
    response = false;
    request = true;
  }

  public DiffContext copyWithMethod(PathItem.HttpMethod method) {
    return copy().setMethod(method);
  }

  public DiffContext copyWithRequired(boolean required) {
    return copy().setRequired(required);
  }

  public DiffContext copyAsRequest() {
    return copy().setRequest();
  }

  public DiffContext copyAsResponse() {
    return copy().setResponse();
  }

  private DiffContext setRequest() {
    this.request = true;
    this.response = false;
    return this;
  }

  private DiffContext setResponse() {
    this.response = true;
    this.request = false;
    return this;
  }

  public boolean isResponse() {
    return this.response;
  }

  public boolean isRequest() {
    return this.request;
  }

  public String getUrl() {
    return url;
  }

  public DiffContext setUrl(String url) {
    this.url = url;
    return this;
  }

  public PathItem.HttpMethod getMethod() {
    return method;
  }

  private DiffContext setMethod(PathItem.HttpMethod method) {
    this.method = method;
    return this;
  }

  private DiffContext copy() {
    DiffContext context = new DiffContext();
    context.url = this.url;
    context.parameters = this.parameters;
    context.method = this.method;
    context.response = this.response;
    context.request = this.request;
    context.required = this.required;
    return context;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public DiffContext setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
    return this;
  }

  public Boolean isRequired() {
    return required;
  }

  private DiffContext setRequired(boolean required) {
    this.required = required;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    DiffContext that = (DiffContext) o;

    return new EqualsBuilder()
        .append(response, that.response)
        .append(request, that.request)
        .append(url, that.url)
        .append(parameters, that.parameters)
        .append(method, that.method)
        .append(required, that.required)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(url)
        .append(parameters)
        .append(method)
        .append(response)
        .append(request)
        .append(required)
        .toHashCode();
  }
}
