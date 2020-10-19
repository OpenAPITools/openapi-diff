package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;

import io.swagger.v3.oas.models.Components;
import java.util.Optional;
import org.openapitools.openapidiff.core.model.ChangedMetadata;
import org.openapitools.openapidiff.core.model.DiffContext;

public class MetadataDiff {

  private final Components leftComponents;
  private final Components rightComponents;
  private final OpenApiDiff openApiDiff;

  public MetadataDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
    this.leftComponents =
        openApiDiff.getOldSpecOpenApi() != null
            ? openApiDiff.getOldSpecOpenApi().getComponents()
            : null;
    this.rightComponents =
        openApiDiff.getNewSpecOpenApi() != null
            ? openApiDiff.getNewSpecOpenApi().getComponents()
            : null;
  }

  public Optional<ChangedMetadata> diff(String left, String right, DiffContext context) {
    return isChanged(new ChangedMetadata().setLeft(left).setRight(right));
  }
}
