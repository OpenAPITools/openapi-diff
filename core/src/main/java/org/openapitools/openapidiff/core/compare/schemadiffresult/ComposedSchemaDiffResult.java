package org.openapitools.openapidiff.core.compare.schemadiffresult;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.openapitools.openapidiff.core.compare.MapKeyDiff;
import org.openapitools.openapidiff.core.compare.OpenApiDiff;
import org.openapitools.openapidiff.core.model.ChangedOneOfSchema;
import org.openapitools.openapidiff.core.model.ChangedSchema;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.utils.RefPointer;
import org.openapitools.openapidiff.core.utils.RefType;

/** Created by adarsh.sharma on 20/12/17. */
public class ComposedSchemaDiffResult extends SchemaDiffResult {
  private static RefPointer<Schema> refPointer = new RefPointer<>(RefType.SCHEMAS);

  public ComposedSchemaDiffResult(OpenApiDiff openApiDiff) {
    super(openApiDiff);
  }

  @Override
  public <T extends Schema<X>, X> Optional<ChangedSchema> diff(
      HashSet<String> refSet,
      Components leftComponents,
      Components rightComponents,
      T left,
      T right,
      DiffContext context) {
    if (left instanceof ComposedSchema) {
      ComposedSchema leftComposedSchema = (ComposedSchema) left;
      ComposedSchema rightComposedSchema = (ComposedSchema) right;
      if (CollectionUtils.isNotEmpty(leftComposedSchema.getOneOf())
          || CollectionUtils.isNotEmpty(rightComposedSchema.getOneOf())) {

        Discriminator leftDis = leftComposedSchema.getDiscriminator();
        Discriminator rightDis = rightComposedSchema.getDiscriminator();
        if ((leftDis == null && rightDis != null)
            || (leftDis != null && rightDis == null)
            || (leftDis != null
                && rightDis != null
                && ((leftDis.getPropertyName() == null && rightDis.getPropertyName() != null)
                    || (leftDis.getPropertyName() != null && rightDis.getPropertyName() == null)
                    || (leftDis.getPropertyName() != null
                        && rightDis.getPropertyName() != null
                        && !leftDis.getPropertyName().equals(rightDis.getPropertyName()))))) {
          changedSchema.setOldSchema(left);
          changedSchema.setNewSchema(right);
          changedSchema.setDiscriminatorPropertyChanged(true);
          changedSchema.setContext(context);
          return Optional.of(changedSchema);
        }

        Map<String, String> leftMapping = getMapping(leftComposedSchema);
        Map<String, String> rightMapping = getMapping(rightComposedSchema);

        MapKeyDiff<String, Schema> mappingDiff =
            MapKeyDiff.diff(
                getSchema(leftComponents, leftMapping), getSchema(rightComponents, rightMapping));
        Map<String, ChangedSchema> changedMapping = new LinkedHashMap<>();
        for (String key : mappingDiff.getSharedKey()) {
          Schema leftSchema = new Schema();
          leftSchema.set$ref(leftMapping.get(key));
          Schema rightSchema = new Schema();
          rightSchema.set$ref(rightMapping.get(key));
          Optional<ChangedSchema> changedSchema =
              openApiDiff
                  .getSchemaDiff()
                  .diff(refSet, leftSchema, rightSchema, context.copyWithRequired(true));
          changedSchema.ifPresent(schema -> changedMapping.put(key, schema));
        }
        changedSchema.setOneOfSchema(
            new ChangedOneOfSchema(leftMapping, rightMapping, context)
                .setIncreased(mappingDiff.getIncreased())
                .setMissing(mappingDiff.getMissing())
                .setChanged(changedMapping));
      }
      return super.diff(refSet, leftComponents, rightComponents, left, right, context);
    } else {
      return openApiDiff.getSchemaDiff().getTypeChangedSchema(left, right, context);
    }
  }

  private Map<String, Schema> getSchema(Components components, Map<String, String> mapping) {
    Map<String, Schema> result = new LinkedHashMap<>();
    mapping.forEach(
        (key, value) -> result.put(key, refPointer.resolveRef(components, new Schema(), value)));
    return result;
  }

  private Map<String, String> getMapping(ComposedSchema composedSchema) {
    Map<String, String> reverseMapping = new LinkedHashMap<>();
    for (Schema schema : composedSchema.getOneOf()) {
      String ref = schema.get$ref();
      if (ref == null) {
        throw new IllegalArgumentException("invalid oneOf schema");
      }
      String schemaName = refPointer.getRefName(ref);
      if (schemaName == null) {
        throw new IllegalArgumentException("invalid schema: " + ref);
      }
      reverseMapping.put(ref, schemaName);
    }

    if (composedSchema.getDiscriminator() != null
        && composedSchema.getDiscriminator().getMapping() != null) {
      for (String ref : composedSchema.getDiscriminator().getMapping().keySet()) {
        reverseMapping.put(composedSchema.getDiscriminator().getMapping().get(ref), ref);
      }
    }

    return reverseMapping.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
  }
}
