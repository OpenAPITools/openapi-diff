package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedApiResponse;
import com.qdesrame.openapi.diff.model.ChangedResponse;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by adarsh.sharma on 04/01/18.
 */
public class ApiResponseDiff {
    private OpenApiDiff openApiDiff;

    public ApiResponseDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
    }

    public Optional<ChangedApiResponse> diff(ApiResponses left, ApiResponses right) {
        MapKeyDiff<String, ApiResponse> responseMapKeyDiff = MapKeyDiff.diff(left, right);
        ChangedApiResponse changedApiResponse = new ChangedApiResponse(left, right);
        changedApiResponse.setAddResponses(responseMapKeyDiff.getIncreased());
        changedApiResponse.setMissingResponses(responseMapKeyDiff.getMissing());
        List<String> sharedResponseCodes = responseMapKeyDiff.getSharedKey();

        Map<String, ChangedResponse> resps = new HashMap<>();
        for (String responseCode : sharedResponseCodes) {
            openApiDiff.getResponseDiff().diff(left.get(responseCode), right.get(responseCode))
                    .ifPresent(changedResponse -> resps.put(responseCode, changedResponse));
        }
        changedApiResponse.setChangedResponses(resps);
        return changedApiResponse.isDiff() ? Optional.of(changedApiResponse) : Optional.empty();
    }
}
