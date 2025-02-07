package org.openapitools.openapidiff.core;

import io.swagger.v3.oas.models.PathItem.HttpMethod;
import java.util.Optional;
import javax.annotation.Nullable;
import org.openapitools.openapidiff.core.model.ChangedApiResponse;
import org.openapitools.openapidiff.core.model.ChangedContent;
import org.openapitools.openapidiff.core.model.ChangedHeaders;
import org.openapitools.openapidiff.core.model.ChangedMediaType;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;
import org.openapitools.openapidiff.core.model.ChangedRequestBody;
import org.openapitools.openapidiff.core.model.ChangedResponse;
import org.openapitools.openapidiff.core.model.ChangedSchema;

public class ChangesResolver {

  @Nullable
  public static ChangedOperation getChangedOperation(
      ChangedOpenApi changedOpenApi, HttpMethod method, String path) {
    return changedOpenApi.getChangedOperations().stream()
        .filter(
            operation ->
                operation.getHttpMethod().equals(method) && operation.getPathUrl().equals(path))
        .findFirst()
        .orElse(null);
  }

  @Nullable
  public static ChangedHeaders getChangedResponseHeaders(
      ChangedOpenApi changedOpenApi, HttpMethod method, String path, String responseCode) {
    return Optional.ofNullable(getChangedOperation(changedOpenApi, method, path))
        .map(ChangedOperation::getApiResponses)
        .map(ChangedApiResponse::getChanged)
        .map(responses -> responses.get(responseCode))
        .map(ChangedResponse::getHeaders)
        .orElse(null);
  }

  @Nullable
  public static ChangedSchema getRequestBodyChangedSchema(
      ChangedOperation changedOperation, String mediaType) {
    return Optional.ofNullable(changedOperation)
        .map(ChangedOperation::getRequestBody)
        .map(ChangedRequestBody::getContent)
        .map(ChangedContent::getChanged)
        .map(changedMediaTypes -> changedMediaTypes.get(mediaType))
        .map(ChangedMediaType::getSchema)
        .orElse(null);
  }
}
