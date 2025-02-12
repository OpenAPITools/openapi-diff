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
import org.openapitools.openapidiff.core.model.ChangedParameter;
import org.openapitools.openapidiff.core.model.ChangedParameters;
import org.openapitools.openapidiff.core.model.ChangedRequestBody;
import org.openapitools.openapidiff.core.model.ChangedResponse;
import org.openapitools.openapidiff.core.model.ChangedSchema;

public class ChangesResolver {

  /**
   * Get the ChangedOperation for the given method and path.
   *
   * @param changedOpenApi the ChangedOpenApi object
   * @param method the HTTP method
   * @param path the path
   * @return the ChangedOperation object
   */
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

  /**
   * Get the ChangedParameter for the given method, path, and parameter name.
   *
   * @param changedOpenApi the ChangedOpenApi object
   * @param method the HTTP method
   * @param path the path
   * @param parameterName the parameter name
   * @return the ChangedParameter object
   */
  @Nullable
  public static ChangedParameter getChangedParameter(
      ChangedOpenApi changedOpenApi, HttpMethod method, String path, String parameterName) {
    ChangedOperation changedOperation = getChangedOperation(changedOpenApi, method, path);

    if (changedOperation == null) {
      return null;
    }

    return Optional.ofNullable(changedOperation.getParameters())
        .map(ChangedParameters::getChanged)
        .flatMap(
            changedParameters ->
                changedParameters.stream()
                    .filter(changedParameter -> changedParameter.getName().equals(parameterName))
                    .findFirst())
        .orElse(null);
  }

  /**
   * Get the ChangedHeaders for the given method, path, and response code.
   *
   * @param changedOpenApi the ChangedOpenApi object
   * @param method the HTTP method
   * @param path the path
   * @param responseCode the response code
   * @return the ChangedHeaders object
   */
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

  /**
   * Get the ChangedSchema for the given method, path, and media type.
   *
   * @param changedOpenApi the ChangedOpenApi object
   * @param method the HTTP method
   * @param path the path
   * @param mediaType the media type
   * @return the ChangedSchema object
   */
  @Nullable
  public static ChangedSchema getRequestBodyChangedSchema(
      ChangedOpenApi changedOpenApi, HttpMethod method, String path, String mediaType) {
    ChangedOperation changedOperation = getChangedOperation(changedOpenApi, method, path);

    if (changedOperation == null) {
      return null;
    }

    return Optional.ofNullable(changedOperation)
        .map(ChangedOperation::getRequestBody)
        .map(ChangedRequestBody::getContent)
        .map(ChangedContent::getChanged)
        .map(changedMediaTypes -> changedMediaTypes.get(mediaType))
        .map(ChangedMediaType::getSchema)
        .orElse(null);
  }
}
