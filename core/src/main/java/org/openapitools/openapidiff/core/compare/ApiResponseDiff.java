package org.openapitools.openapidiff.core.compare;

import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedApiResponse;
import org.openapitools.openapidiff.core.model.ChangedResponse;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredBuilder;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;

public class ApiResponseDiff {
  private final OpenApiDiff openApiDiff;

  public ApiResponseDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  public DeferredChanged<ChangedApiResponse> diff(
      @Nullable ApiResponses left, @Nullable ApiResponses right, DiffContext context) {
    MapKeyDiff<String, ApiResponse> responseMapKeyDiff = MapKeyDiff.diff(left, right);
    List<String> sharedResponseCodes = responseMapKeyDiff.getSharedKey();
    Map<String, ChangedResponse> resps = new LinkedHashMap<>();
    DeferredBuilder<Changed> builder = new DeferredBuilder<>();

    for (String responseCode : sharedResponseCodes) {
      builder
          .with(
              openApiDiff
                  .getResponseDiff()
                  .diff(
                      left != null ? left.get(responseCode) : null,
                      right != null ? right.get(responseCode) : null,
                      context))
          .ifPresent(changedResponse -> resps.put(responseCode, changedResponse));
    }
    ChangedApiResponse changedApiResponse =
        new ChangedApiResponse(left, right, context)
            .setIncreased(responseMapKeyDiff.getIncreased())
            .setMissing(responseMapKeyDiff.getMissing())
            .setChanged(resps);
    builder
        .with(
            openApiDiff
                .getExtensionsDiff()
                .diff(
                    left != null ? left.getExtensions() : null,
                    right != null ? right.getExtensions() : null,
                    context))
        .ifPresent(changedApiResponse::setExtensions);
    return builder.buildIsChanged(changedApiResponse);
  }
}
