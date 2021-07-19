package org.openapitools.openapidiff.core.model.deferred;

import io.swagger.v3.oas.models.media.Schema;
import org.openapitools.openapidiff.core.compare.CacheKey;
import org.openapitools.openapidiff.core.compare.OpenApiDiff;
import org.openapitools.openapidiff.core.model.ChangedSchema;

public class SchemaDiffOperation {
  final OpenApiDiff openApiDiff;
  final RecursiveSchemaSet refSet;
  final CacheKey key;
  final Schema left;
  final Schema right;
  boolean processed;

  PendingChanged<ChangedSchema> diffResult = new PendingChanged<>();

  SchemaDiffOperation(
      OpenApiDiff openApiDiff, RecursiveSchemaSet refSet, CacheKey key, Schema left, Schema right) {
    this.openApiDiff = openApiDiff;
    this.refSet = refSet;
    this.key = key;
    this.left = left;
    this.right = right;
  }
}
