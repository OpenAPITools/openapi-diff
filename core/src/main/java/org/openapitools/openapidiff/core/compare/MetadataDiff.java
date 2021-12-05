package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;

import java.util.Optional;
import org.openapitools.openapidiff.core.model.ChangedMetadata;
import org.openapitools.openapidiff.core.model.DiffContext;

public class MetadataDiff {
  public MetadataDiff(OpenApiDiff openApiDiff) {}

  public Optional<ChangedMetadata> diff(String left, String right, DiffContext context) {
    return isChanged(new ChangedMetadata().setLeft(left).setRight(right));
  }
}
