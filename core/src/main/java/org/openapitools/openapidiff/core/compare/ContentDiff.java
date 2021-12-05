package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;
import static org.openapitools.openapidiff.core.utils.ChangedUtils.isUnchanged;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import java.util.*;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedContent;
import org.openapitools.openapidiff.core.model.ChangedMediaType;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredBuilder;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;

public class ContentDiff {

  private final OpenApiDiff openApiDiff;

  public ContentDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  public DeferredChanged<ChangedContent> diff(Content left, Content right, DiffContext context) {
    DeferredBuilder<Changed> builder = new DeferredBuilder<>();

    MapKeyDiff<String, MediaType> mediaTypeDiff = MapKeyDiff.diff(left, right);
    List<String> sharedMediaTypes = mediaTypeDiff.getSharedKey();
    Map<String, ChangedMediaType> changedMediaTypes = new LinkedHashMap<>();

    sharedMediaTypes.stream()
        .forEach(
            (mediaTypeKey) -> {
              MediaType oldMediaType = left.get(mediaTypeKey);
              MediaType newMediaType = right.get(mediaTypeKey);

              ChangedMediaType changedMediaType =
                  new ChangedMediaType(oldMediaType.getSchema(), newMediaType.getSchema(), context);

              builder
                  .with(
                      openApiDiff
                          .getSchemaDiff()
                          .diff(
                              oldMediaType.getSchema(),
                              newMediaType.getSchema(),
                              context.copyWithRequired(true)))
                  .ifPresent(
                      (value) -> {
                        changedMediaType.setSchema(value);
                        if (!isUnchanged(changedMediaType)) {
                          changedMediaTypes.put(mediaTypeKey, changedMediaType);
                        }
                      });
            });

    return builder
        .build()
        .mapOptional(
            (value) ->
                isChanged(
                    new ChangedContent(left, right, context)
                        .setIncreased(mediaTypeDiff.getIncreased())
                        .setMissing(mediaTypeDiff.getMissing())
                        .setChanged(changedMediaTypes)));
  }
}
