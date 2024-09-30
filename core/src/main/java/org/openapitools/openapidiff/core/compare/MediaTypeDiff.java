package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;

import io.swagger.v3.oas.models.media.MediaType;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedExample;
import org.openapitools.openapidiff.core.model.ChangedExamples;
import org.openapitools.openapidiff.core.model.ChangedMediaType;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredBuilder;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;

public class MediaTypeDiff {

  private final OpenApiDiff openApiDiff;

  public MediaTypeDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  public DeferredChanged<ChangedMediaType> diff(
      MediaType left, MediaType right, DiffContext context) {
    DeferredBuilder<Changed> builder = new DeferredBuilder<>();

    ChangedMediaType changedMediaType =
        new ChangedMediaType(left.getSchema(), right.getSchema(), context)
            .setExample(new ChangedExample(left.getExample(), right.getExample()))
            .setExamples(new ChangedExamples(left.getExamples(), right.getExamples()));

    builder
        .with(
            openApiDiff
                .getSchemaDiff()
                .diff(left.getSchema(), right.getSchema(), context.copyWithRequired(true)))
        .ifPresent(changedMediaType::setSchema);

    return builder.build().mapOptional(value -> isChanged(changedMediaType));
  }
}
