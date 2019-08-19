package com.qdesrame.openapi.diff.compare;

import static com.qdesrame.openapi.diff.utils.ChangedUtils.isChanged;

import com.qdesrame.openapi.diff.model.ChangedMetadata;
import com.qdesrame.openapi.diff.model.DiffContext;
import io.swagger.v3.oas.models.Components;
import java.util.Optional;

public class MetadataDiff {

  private Components leftComponents;
  private Components rightComponents;
  private OpenApiDiff openApiDiff;

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
