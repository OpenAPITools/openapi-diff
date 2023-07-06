package org.openapitools.openapidiff.core.model;

import static org.openapitools.openapidiff.core.model.Changed.result;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChangedOperation implements ComposedChanged {
  private Operation oldOperation;
  private Operation newOperation;
  private String pathUrl;
  private PathItem.HttpMethod httpMethod;
  private ChangedMetadata summary;
  private ChangedMetadata description;
  private ChangedMetadata operationId;
  private boolean deprecated;
  private ChangedParameters parameters;
  private ChangedRequestBody requestBody;
  private ChangedApiResponse apiResponses;
  private ChangedSecurityRequirements securityRequirements;
  private ChangedExtensions extensions;

  public ChangedOperation(
      String pathUrl,
      PathItem.HttpMethod httpMethod,
      Operation oldOperation,
      Operation newOperation) {
    this.httpMethod = httpMethod;
    this.pathUrl = pathUrl;
    this.oldOperation = oldOperation;
    this.newOperation = newOperation;
  }

  @Override
  public List<Changed> getChangedElements() {
    return Arrays.asList(
        summary,
        description,
        operationId,
        parameters,
        requestBody,
        apiResponses,
        securityRequirements,
        extensions);
  }

  @Override
  public DiffResult isCoreChanged() {
    // TODO BETTER HANDLING FOR DEPRECIATION
    if (deprecated) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.NO_CHANGES;
  }

  public DiffResult resultApiResponses() {
    return result(apiResponses);
  }

  public DiffResult resultRequestBody() {
    return requestBody == null ? DiffResult.NO_CHANGES : requestBody.isChanged();
  }
  public DiffResult resultSecurityRequirements() {
    return securityRequirements == null ? DiffResult.NO_CHANGES : securityRequirements.isChanged();
  }

  public Operation getOldOperation() {
    return this.oldOperation;
  }

  public Operation getNewOperation() {
    return this.newOperation;
  }

  public String getPathUrl() {
    return this.pathUrl;
  }

  public PathItem.HttpMethod getHttpMethod() {
    return this.httpMethod;
  }

  public ChangedMetadata getSummary() {
    return this.summary;
  }

  public ChangedMetadata getDescription() {
    return this.description;
  }

  public ChangedMetadata getOperationId() {
    return this.operationId;
  }

  public boolean isDeprecated() {
    return this.deprecated;
  }

  public ChangedParameters getParameters() {
    return this.parameters;
  }

  public ChangedRequestBody getRequestBody() {
    return this.requestBody;
  }

  public ChangedApiResponse getApiResponses() {
    return this.apiResponses;
  }

  public ChangedSecurityRequirements getSecurityRequirements() {
    return this.securityRequirements;
  }

  public ChangedExtensions getExtensions() {
    return this.extensions;
  }

  public ChangedOperation setOldOperation(final Operation oldOperation) {
    this.oldOperation = oldOperation;
    return this;
  }

  public ChangedOperation setNewOperation(final Operation newOperation) {
    this.newOperation = newOperation;
    return this;
  }

  public ChangedOperation setPathUrl(final String pathUrl) {
    this.pathUrl = pathUrl;
    return this;
  }

  public ChangedOperation setHttpMethod(final PathItem.HttpMethod httpMethod) {
    this.httpMethod = httpMethod;
    return this;
  }

  public ChangedOperation setSummary(final ChangedMetadata summary) {
    this.summary = summary;
    return this;
  }

  public ChangedOperation setDescription(final ChangedMetadata description) {
    this.description = description;
    return this;
  }

  public ChangedOperation setOperationId(final ChangedMetadata operationId) {
    this.operationId = operationId;
    return this;
  }

  public ChangedOperation setDeprecated(final boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  public ChangedOperation setParameters(final ChangedParameters parameters) {
    this.parameters = parameters;
    return this;
  }

  public ChangedOperation setRequestBody(final ChangedRequestBody requestBody) {
    this.requestBody = requestBody;
    return this;
  }

  public ChangedOperation setApiResponses(final ChangedApiResponse apiResponses) {
    this.apiResponses = apiResponses;
    return this;
  }

  public ChangedOperation setSecurityRequirements(
      final ChangedSecurityRequirements securityRequirements) {
    this.securityRequirements = securityRequirements;
    return this;
  }

  public ChangedOperation setExtensions(final ChangedExtensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedOperation that = (ChangedOperation) o;
    return deprecated == that.deprecated
        && Objects.equals(oldOperation, that.oldOperation)
        && Objects.equals(newOperation, that.newOperation)
        && Objects.equals(pathUrl, that.pathUrl)
        && httpMethod == that.httpMethod
        && Objects.equals(summary, that.summary)
        && Objects.equals(description, that.description)
        && Objects.equals(operationId, that.operationId)
        && Objects.equals(parameters, that.parameters)
        && Objects.equals(requestBody, that.requestBody)
        && Objects.equals(apiResponses, that.apiResponses)
        && Objects.equals(securityRequirements, that.securityRequirements)
        && Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        oldOperation,
        newOperation,
        pathUrl,
        httpMethod,
        summary,
        description,
        operationId,
        deprecated,
        parameters,
        requestBody,
        apiResponses,
        securityRequirements,
        extensions);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedOperation(oldOperation="
        + this.getOldOperation()
        + ", newOperation="
        + this.getNewOperation()
        + ", pathUrl="
        + this.getPathUrl()
        + ", httpMethod="
        + this.getHttpMethod()
        + ", summary="
        + this.getSummary()
        + ", description="
        + this.getDescription()
        + ", operationId="
        + this.getOperationId()
        + ", deprecated="
        + this.isDeprecated()
        + ", parameters="
        + this.getParameters()
        + ", requestBody="
        + this.getRequestBody()
        + ", apiResponses="
        + this.getApiResponses()
        + ", securityRequirements="
        + this.getSecurityRequirements()
        + ", extensions="
        + this.getExtensions()
        + ")";
  }
}
