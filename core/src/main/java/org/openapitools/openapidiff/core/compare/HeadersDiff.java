package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;

import io.swagger.v3.oas.models.headers.Header;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openapitools.openapidiff.core.model.ChangedHeader;
import org.openapitools.openapidiff.core.model.ChangedHeaders;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredBuilder;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;

public class HeadersDiff {
  private final OpenApiDiff openApiDiff;

  public HeadersDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  public DeferredChanged<ChangedHeaders> diff(
      Map<String, Header> left, Map<String, Header> right, DiffContext context) {
    MapKeyDiff<String, Header> headerMapDiff = MapKeyDiff.diff(left, right);
    List<String> sharedHeaderKeys = headerMapDiff.getSharedKey();

    Map<String, ChangedHeader> changed = new LinkedHashMap<>();
    DeferredBuilder<ChangedHeader> builder = new DeferredBuilder<>();
    for (String headerKey : sharedHeaderKeys) {
      Header oldHeader = left.get(headerKey);
      Header newHeader = right.get(headerKey);
      builder
          .with(openApiDiff.getHeaderDiff().diff(oldHeader, newHeader, context))
          .ifPresent(changedHeader -> changed.put(headerKey, changedHeader));
    }
    return builder
        .build()
        .mapOptional(
            (value) ->
                isChanged(
                    new ChangedHeaders(left, right, context)
                        .setIncreased(headerMapDiff.getIncreased())
                        .setMissing(headerMapDiff.getMissing())
                        .setChanged(changed)));
  }
}
