package com.qdesrame.openapi.diff.model;

import static com.qdesrame.openapi.diff.model.Changed.result;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangedOperation implements ComposedChanged {

  private Operation oldOperation;
  private Operation newOperation;

  private String pathUrl;
  private PathItem.HttpMethod httpMethod;
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
  public List<Changed> getChangedElements() {
    return Arrays.asList(
        summary,
        description,
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
}
