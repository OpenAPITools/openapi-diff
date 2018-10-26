package com.qdesrame.openapi.diff.model;

import static com.qdesrame.openapi.diff.model.Changed.result;

import com.qdesrame.openapi.diff.model.schema.ChangedExtensions;
import com.qdesrame.openapi.diff.utils.ChangedUtils;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangedOperation implements Changed {
  private String pathUrl;
  private PathItem.HttpMethod httpMethod;
  private Operation oldOperation;
  private Operation newOperation;
  private ChangedMetadata summary;
  private ChangedMetadata description;
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
  public DiffResult isChanged() {
    // TODO BETTER HANDLING FOR DEPRECIATION
    if (!deprecated
        && resultParameters().isUnchanged()
        && resultRequestBody().isUnchanged()
        && resultApiResponses().isUnchanged()
        && resultSecurityRequirements().isUnchanged()
        && resultExtensions().isUnchanged()
        && resultDescription().isUnchanged()
        && resultSummary().isUnchanged()) {
      return DiffResult.NO_CHANGES;
    }
    if (resultDescription().isMetaChanged()
        || resultSummary().isMetaChanged()
        || resultExtensions().isMetaChanged()) {
      return DiffResult.METADATA;
    }
    if (resultParameters().isCompatible()
        && resultRequestBody().isCompatible()
        && resultApiResponses().isCompatible()
        && resultSecurityRequirements().isCompatible()
        && ChangedUtils.isCompatible(extensions)) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }

  public DiffResult resultParameters() {
    return result(parameters);
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

  public DiffResult resultDescription() {
    return result(description);
  }

  public DiffResult resultSummary() {
    return result(summary);
  }

  public DiffResult resultExtensions() {
    return result(extensions);
  }
}
