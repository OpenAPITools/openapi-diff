package com.qdesrame.openapi.diff.compare.schemadiffresult;

import com.qdesrame.openapi.diff.compare.MapKeyDiff;
import com.qdesrame.openapi.diff.compare.OpenApiDiff;
import com.qdesrame.openapi.diff.model.ChangedOneOfSchema;
import com.qdesrame.openapi.diff.model.ChangedSchema;
import com.qdesrame.openapi.diff.model.DiffContext;
import com.qdesrame.openapi.diff.utils.RefPointer;
import com.qdesrame.openapi.diff.utils.RefType;
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

/** Created by adarsh.sharma on 20/12/17. */
public class ComposedSchemaDiffResult extends SchemaDiffResult {
  private static RefPointer<Schema> refPointer = new RefPointer<>(RefType.SCHEMAS);

  public ComposedSchemaDiffResult(OpenApiDiff openApiDiff) {
    super(openApiDiff);
  }

  @Override
  public Optional<ChangedSchema> diff(
      HashSet<String> refSet,
      Components leftComponents,
      Components rightComponents,
      Schema left,
      Schema right,
      DiffContext context) {
    if (left instanceof ComposedSchema) {
      ComposedSchema leftComposedSchema = (ComposedSchema) left;
      ComposedSchema rightComposedSchema = (ComposedSchema) right;
      if (CollectionUtils.isNotEmpty(leftComposedSchema.getOneOf())
          || CollectionUtils.isNotEmpty(rightComposedSchema.getOneOf())) {

        Discriminator leftDis = leftComposedSchema.getDiscriminator();
        Discriminator rightDis = rightComposedSchema.getDiscriminator();
        if (leftDis == null
            || rightDis == null
            || leftDis.getPropertyName() == null
            || rightDis.getPropertyName() == null) {
          throw new IllegalArgumentException(
              "discriminator or property not found for oneOf schema");
        } else if (!leftDis.getPropertyName().equals(rightDis.getPropertyName())
            || (CollectionUtils.isEmpty(leftComposedSchema.getOneOf())
                || CollectionUtils.isEmpty(rightComposedSchema.getOneOf()))) {
          changedSchema.setOldSchema(left);
          changedSchema.setNewSchema(right);
          changedSchema.setDiscriminatorPropertyChanged(true);
          changedSchema.setContext(context);
          return Optional.of(changedSchema);
        }

        Map<String, String> leftMapping = getMapping(leftComposedSchema);
        Map<String, String> rightMapping = getMapping(rightComposedSchema);

        ChangedOneOfSchema changedOneOfSchema =
            new ChangedOneOfSchema(leftMapping, rightMapping, context);
        MapKeyDiff<String, Schema> mappingDiff =
            MapKeyDiff.diff(
                getSchema(leftComponents, leftMapping), getSchema(rightComponents, rightMapping));
        changedOneOfSchema.setIncreasedMapping(mappingDiff.getIncreased());
        changedOneOfSchema.setMissingMapping(mappingDiff.getMissing());

        Map<String, ChangedSchema> changedMapping = new LinkedHashMap<>();
        changedOneOfSchema.setChangedMapping(changedMapping);

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
        changedSchema.setChangedOneOfSchema(changedOneOfSchema);
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

    if (composedSchema.getDiscriminator().getMapping() != null) {
      for (String ref : composedSchema.getDiscriminator().getMapping().keySet()) {
        reverseMapping.put(composedSchema.getDiscriminator().getMapping().get(ref), ref);
      }
    }

    return reverseMapping
        .entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
  }
}
