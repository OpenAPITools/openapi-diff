package org.openapitools.openapidiff.core.compare.schemadiffresult;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import org.openapitools.openapidiff.core.compare.OpenApiDiff;
import org.openapitools.openapidiff.core.model.ChangedSchema;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;
import org.openapitools.openapidiff.core.model.deferred.RecursiveSchemaSet;

/** Created by adarsh.sharma on 18/12/17. */
public class ArraySchemaDiffResult extends SchemaDiffResult {
  public ArraySchemaDiffResult(OpenApiDiff openApiDiff) {
    super("array", openApiDiff);
  }

  @Override
  public <T extends Schema<X>, X> DeferredChanged<ChangedSchema> diff(
      RecursiveSchemaSet refSet,
      Components leftComponents,
      Components rightComponents,
      T left,
      T right,
      DiffContext context) {
    ArraySchema leftArraySchema = (ArraySchema) left;
    ArraySchema rightArraySchema = (ArraySchema) right;

    DeferredChanged<ChangedSchema> superSchemaDiff =
        super.diff(refSet, leftComponents, rightComponents, left, right, context)
            .flatMap(
                (changeSchemaOptional) -> {
                  DeferredChanged<ChangedSchema> itemsDiff =
                      openApiDiff
                          .getSchemaDiff()
                          .diff(
                              refSet,
                              leftArraySchema.getItems(),
                              rightArraySchema.getItems(),
                              context.copyWithRequired(true));
                  itemsDiff.ifPresent(changedSchema::setItems);
                  return itemsDiff;
                });

    return superSchemaDiff.mapOptional(schemaOptional -> isApplicable(context));
  }
}
