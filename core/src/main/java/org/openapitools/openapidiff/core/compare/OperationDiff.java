package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.openapitools.openapidiff.core.model.ChangedOperation;
import org.openapitools.openapidiff.core.model.ChangedParameters;
import org.openapitools.openapidiff.core.model.DiffContext;

/** Created by adarsh.sharma on 04/01/18. */
public class OperationDiff {
  private final OpenApiDiff openApiDiff;

  public OperationDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  public Optional<ChangedOperation> diff(
      Operation oldOperation, Operation newOperation, DiffContext context) {
    ChangedOperation changedOperation =
        new ChangedOperation(context.getUrl(), context.getMethod(), oldOperation, newOperation);

    openApiDiff
        .getMetadataDiff()
        .diff(oldOperation.getSummary(), newOperation.getSummary(), context)
        .ifPresent(changedOperation::setSummary);
    openApiDiff
        .getMetadataDiff()
        .diff(oldOperation.getDescription(), newOperation.getDescription(), context)
        .ifPresent(changedOperation::setDescription);
    openApiDiff
        .getMetadataDiff()
        .diff(oldOperation.getOperationId(), newOperation.getOperationId(), context)
        .ifPresent(changedOperation::setOperationId);
    changedOperation.setDeprecated(
        !Boolean.TRUE.equals(oldOperation.getDeprecated())
            && Boolean.TRUE.equals(newOperation.getDeprecated()));

    if (oldOperation.getRequestBody() != null || newOperation.getRequestBody() != null) {
      openApiDiff
          .getRequestBodyDiff()
          .diff(
              oldOperation.getRequestBody(), newOperation.getRequestBody(), context.copyAsRequest())
          .ifPresent(changedOperation::setRequestBody);
    }

    openApiDiff
        .getParametersDiff()
        .diff(oldOperation.getParameters(), newOperation.getParameters(), context)
        .ifPresent(
            params -> {
              removePathParameters(context.getParameters(), params);
              changedOperation.setParameters(params);
            });

    if (oldOperation.getResponses() != null || newOperation.getResponses() != null) {
      openApiDiff
          .getApiResponseDiff()
          .diff(oldOperation.getResponses(), newOperation.getResponses(), context.copyAsResponse())
          .ifPresent(changedOperation::setApiResponses);
    }

    if (oldOperation.getSecurity() != null || newOperation.getSecurity() != null) {
      openApiDiff
          .getSecurityRequirementsDiff()
          .diff(oldOperation.getSecurity(), newOperation.getSecurity(), context)
          .ifPresent(changedOperation::setSecurityRequirements);
    }

    openApiDiff
        .getExtensionsDiff()
        .diff(oldOperation.getExtensions(), newOperation.getExtensions(), context)
        .ifPresent(changedOperation::setExtensions);

    return isChanged(changedOperation);
  }

  public void removePathParameters(Map<String, String> pathParameters, ChangedParameters params) {
    pathParameters.forEach(
        (oldParam, newParam) -> {
          removePathParameter(oldParam, params.getMissing());
          removePathParameter(newParam, params.getIncreased());
        });
  }

  public void removePathParameter(String name, List<Parameter> parameters) {
    parameters.stream()
        .filter(p -> "path".equals(p.getIn()) && name.equals(p.getName()))
        .findFirst()
        .ifPresent(parameters::remove);
  }
}
