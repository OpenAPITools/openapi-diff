package org.openapitools.openapidiff.core.compare.schemadiffresult;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashSet;
import java.util.Optional;
import org.openapitools.openapidiff.core.compare.OpenApiDiff;
import org.openapitools.openapidiff.core.model.ChangedSchema;
import org.openapitools.openapidiff.core.model.DiffContext;

/** Created by adarsh.sharma on 18/12/17. */
public class ArraySchemaDiffResult extends SchemaDiffResult {
  public ArraySchemaDiffResult(OpenApiDiff openApiDiff) {
    super("array", openApiDiff);
  }

  @Override
  public <T extends Schema<X>, X> Optional<ChangedSchema> diff(
      HashSet<String> refSet,
      Components leftComponents,
      Components rightComponents,
      T left,
      T right,
      DiffContext context) {
    ArraySchema leftArraySchema = (ArraySchema) left;
    ArraySchema rightArraySchema = (ArraySchema) right;
    super.diff(refSet, leftComponents, rightComponents, left, right, context);
    openApiDiff
        .getSchemaDiff()
        .diff(
            refSet,
            leftArraySchema.getItems(),
            rightArraySchema.getItems(),
            context.copyWithRequired(true))
        .ifPresent(changedSchema::setItems);
    return isApplicable(context);
  }
}
