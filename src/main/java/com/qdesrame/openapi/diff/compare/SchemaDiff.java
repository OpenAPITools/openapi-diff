package com.qdesrame.openapi.diff.compare;

import static java.util.Optional.ofNullable;

import com.qdesrame.openapi.diff.compare.schemadiffresult.ArraySchemaDiffResult;
import com.qdesrame.openapi.diff.compare.schemadiffresult.ComposedSchemaDiffResult;
import com.qdesrame.openapi.diff.compare.schemadiffresult.SchemaDiffResult;
import com.qdesrame.openapi.diff.model.ChangedSchema;
import com.qdesrame.openapi.diff.model.DiffContext;
import com.qdesrame.openapi.diff.utils.RefPointer;
import com.qdesrame.openapi.diff.utils.RefType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.*;

public class SchemaDiff extends ReferenceDiffCache<Schema, ChangedSchema> {

  private static RefPointer<Schema> refPointer = new RefPointer<>(RefType.SCHEMAS);
  private static Map<Class<? extends Schema>, Class<? extends SchemaDiffResult>>
      schemaDiffResultClassMap = new LinkedHashMap<>();

  static {
    schemaDiffResultClassMap.put(Schema.class, SchemaDiffResult.class);
    schemaDiffResultClassMap.put(ArraySchema.class, ArraySchemaDiffResult.class);
    schemaDiffResultClassMap.put(ComposedSchema.class, ComposedSchemaDiffResult.class);
  }

  private Components leftComponents;
  private Components rightComponents;
  private OpenApiDiff openApiDiff;

  public SchemaDiff(OpenApiDiff openApiDiff) {
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

  public static SchemaDiffResult getSchemaDiffResult(OpenApiDiff openApiDiff) {
    return getSchemaDiffResult(null, openApiDiff);
  }

  public static SchemaDiffResult getSchemaDiffResult(
      Class<? extends Schema> classType, OpenApiDiff openApiDiff) {
    if (classType == null) {
      classType = Schema.class;
    }

    Class<? extends SchemaDiffResult> aClass = schemaDiffResultClassMap.get(classType);
    try {
      if (aClass == null) {
        aClass = schemaDiffResultClassMap.get(Schema.class);
      }
      if (aClass != null) {
        return aClass.getConstructor(OpenApiDiff.class).newInstance(openApiDiff);
      } else {
        throw new IllegalArgumentException("invalid classType");
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("type " + classType + " is illegal");
    }
  }

  protected static Schema resolveComposedSchema(Components components, Schema schema) {
    if (schema instanceof ComposedSchema) {
      ComposedSchema composedSchema = (ComposedSchema) schema;
      List<Schema> allOfSchemaList = composedSchema.getAllOf();
      if (allOfSchemaList != null) {
        for (Schema allOfSchema : allOfSchemaList) {
          allOfSchema = refPointer.resolveRef(components, allOfSchema, allOfSchema.get$ref());
          allOfSchema = resolveComposedSchema(components, allOfSchema);
          schema = addSchema(schema, allOfSchema);
        }
        composedSchema.setAllOf(null);
      }
    }
    return schema;
  }

  protected static Schema addSchema(Schema<?> schema, Schema<?> fromSchema) {
    if (fromSchema.getProperties() != null) {
      if (schema.getProperties() == null) {
        schema.setProperties(fromSchema.getProperties());
      } else {
        schema.getProperties().putAll(fromSchema.getProperties());
      }
    }

    if (fromSchema.getRequired() != null) {
      if (schema.getRequired() == null) {
        schema.setRequired(fromSchema.getRequired());
      } else {
        schema.getRequired().addAll(fromSchema.getRequired());
      }
    }
    // TODO copy other things from fromSchema
    return schema;
  }

  private static String getSchemaRef(Schema schema) {
    return ofNullable(schema).map(Schema::get$ref).orElse(null);
  }

  public Optional<ChangedSchema> diff(
      HashSet<String> refSet, Schema left, Schema right, DiffContext context) {
    if (left == null && right == null) {
      return Optional.empty();
    }
    return cachedDiff(refSet, left, right, getSchemaRef(left), getSchemaRef(right), context);
  }

  public Optional<ChangedSchema> getTypeChangedSchema(
      Schema left, Schema right, DiffContext context) {
    return Optional.of(
        SchemaDiff.getSchemaDiffResult(openApiDiff)
            .getChangedSchema()
            .setOldSchema(left)
            .setNewSchema(right)
            .setChangedType(true)
            .setContext(context));
  }

  @Override
  protected Optional<ChangedSchema> computeDiff(
      HashSet<String> refSet, Schema left, Schema right, DiffContext context) {
    left = refPointer.resolveRef(this.leftComponents, left, getSchemaRef(left));
    right = refPointer.resolveRef(this.rightComponents, right, getSchemaRef(right));

    left = resolveComposedSchema(leftComponents, left);
    right = resolveComposedSchema(rightComponents, right);

    // If type of schemas are different, just set old & new schema, set changedType to true in
    // SchemaDiffResult and
    // return the object
    if ((left == null || right == null)
        || !Objects.equals(left.getType(), right.getType())
        || !Objects.equals(left.getFormat(), right.getFormat())) {
      return getTypeChangedSchema(left, right, context);
    }

    // If schema type is same then get specific SchemaDiffResult and compare the properties
    SchemaDiffResult result = SchemaDiff.getSchemaDiffResult(right.getClass(), openApiDiff);
    return result.diff(refSet, leftComponents, rightComponents, left, right, context);
  }
}
