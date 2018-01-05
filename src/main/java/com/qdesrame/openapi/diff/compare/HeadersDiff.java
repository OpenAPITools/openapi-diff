package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedHeader;
import com.qdesrame.openapi.diff.model.ChangedHeaders;
import io.swagger.v3.oas.models.headers.Header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
public class HeadersDiff {
    private OpenApiDiff openApiDiff;

    public HeadersDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
    }

    public Optional<ChangedHeaders> diff(Map<String, Header> left, Map<String, Header> right) {
        ChangedHeaders changedHeaders = new ChangedHeaders(left, right);
        MapKeyDiff<String, Header> headerMapDiff = MapKeyDiff.diff(left, right);
        changedHeaders.setIncreased(headerMapDiff.getIncreased());
        changedHeaders.setMissing(headerMapDiff.getMissing());
        List<String> sharedHeaderKeys = headerMapDiff.getSharedKey();

        Map<String, ChangedHeader> changed = new HashMap<>();
        for (String headerKey : sharedHeaderKeys) {
            Header oldHeader = left.get(headerKey);
            Header newHeader = right.get(headerKey);
            openApiDiff.getHeaderDiff().diff(oldHeader, newHeader)
                    .ifPresent(changedHeader -> changed.put(headerKey, changedHeader));
        }
        changedHeaders.setChanged(changed);

        return changedHeaders.isDiff() ? Optional.of(changedHeaders) : Optional.empty();
    }
}
