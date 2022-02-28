package org.openapitools.openapidiff.core.compare;

import static java.util.Optional.ofNullable;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.XML;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.openapitools.openapidiff.core.compare.schemadiffresult.ArraySchemaDiffResult;
import org.openapitools.openapidiff.core.compare.schemadiffresult.ComposedSchemaDiffResult;
import org.openapitools.openapidiff.core.compare.schemadiffresult.SchemaDiffResult;
import org.openapitools.openapidiff.core.model.ChangedSchema;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;
import org.openapitools.openapidiff.core.model.deferred.RealizedChanged;
import org.openapitools.openapidiff.core.model.deferred.RecursiveSchemaSet;
import org.openapitools.openapidiff.core.utils.RefPointer;
import org.openapitools.openapidiff.core.utils.RefType;

public class SchemaDiff {

  private static final RefPointer<Schema<?>> refPointer = new RefPointer<>(RefType.SCHEMAS);
  private static final Map<Class<? extends Schema>, Class<? extends SchemaDiffResult>>
      schemaDiffResultClassMap = new LinkedHashMap<>();

  static {
    schemaDiffResultClassMap.put(Schema.class, SchemaDiffResult.class);
    schemaDiffResultClassMap.put(ArraySchema.class, ArraySchemaDiffResult.class);
    schemaDiffResultClassMap.put(ComposedSchema.class, ComposedSchemaDiffResult.class);
  }

  private final Components leftComponents;
  private final Components rightComponents;
  private final OpenApiDiff openApiDiff;

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

  protected static Schema<?> resolveComposedSchema(
      Components components, Schema<?> schema, Set<String> visitedRefs) {
    if (schema instanceof ComposedSchema) {
      ComposedSchema composedSchema = (ComposedSchema) schema;
      List<Schema> composedSchemas = new ArrayList<>();
      Optional.ofNullable(composedSchema.getAllOf()).ifPresent(composedSchemas::addAll);
      Optional.ofNullable(composedSchema.getAnyOf()).ifPresent(composedSchemas::addAll);

      if (!composedSchemas.isEmpty()) {
        for (Schema<?> composed : composedSchemas) {
          if (composed.get$ref() == null || !visitedRefs.contains(composed.get$ref())) {
            Set<String> updatedVisitedRefs = new HashSet<>(visitedRefs);
            updatedVisitedRefs.add(composed.get$ref());
            composed = refPointer.resolveRef(components, composed, composed.get$ref());
            composed = resolveComposedSchema(components, composed, updatedVisitedRefs);
            schema = addSchema(schema, composed);
          }
        }
        composedSchema.setAllOf(null);
        composedSchema.setAnyOf(null);
      }
    }
    return schema;
  }

  protected static Schema<?> addSchema(Schema<?> schema, Schema<?> fromSchema) {
    if (fromSchema.getProperties() != null) {
      if (schema.getProperties() == null) {
        schema.setProperties(new LinkedHashMap<>());
      }
      schema.getProperties().putAll(fromSchema.getProperties());
    }

    if (fromSchema.getRequired() != null) {
      if (schema.getRequired() == null) {
        schema.setRequired(fromSchema.getRequired());
      } else {
        schema.getRequired().addAll(fromSchema.getRequired());
      }
    }

    if (fromSchema.getReadOnly() != null) {
      schema.setReadOnly(fromSchema.getReadOnly());
    }
    if (fromSchema.getWriteOnly() != null) {
      schema.setWriteOnly(fromSchema.getWriteOnly());
    }
    if (fromSchema.getDeprecated() != null) {
      schema.setDeprecated(fromSchema.getDeprecated());
    }
    if (fromSchema.getExclusiveMaximum() != null) {
      schema.setExclusiveMaximum(fromSchema.getExclusiveMaximum());
    }
    if (fromSchema.getExclusiveMinimum() != null) {
      schema.setExclusiveMinimum(fromSchema.getExclusiveMinimum());
    }
    if (fromSchema.getNullable() != null) {
      schema.setNullable(fromSchema.getNullable());
    }
    if (fromSchema.getUniqueItems() != null) {
      schema.setUniqueItems(fromSchema.getUniqueItems());
    }
    if (fromSchema.getDescription() != null) {
      schema.setDescription(fromSchema.getDescription());
    }
    if (fromSchema.getFormat() != null) {
      schema.setFormat(fromSchema.getFormat());
    }
    if (fromSchema.getType() != null) {
      schema.setType(fromSchema.getType());
    }
    if (fromSchema.getEnum() != null) {
      if (schema.getEnum() == null) {
        schema.setEnum(new ArrayList<>());
      }
      //noinspection unchecked
      schema.getEnum().addAll((List) fromSchema.getEnum());
    }
    if (fromSchema.getExtensions() != null) {
      if (schema.getExtensions() == null) {
        schema.setExtensions(new LinkedHashMap<>());
      }
      schema.getExtensions().putAll(fromSchema.getExtensions());
    }
    if (fromSchema instanceof ComposedSchema && schema instanceof ComposedSchema) {
      ComposedSchema composedFromSchema = (ComposedSchema) fromSchema;
      ComposedSchema composedSchema = (ComposedSchema) schema;
      if (composedFromSchema.getOneOf() != null) {
        if (composedSchema.getOneOf() == null) {
          composedSchema.setOneOf(new ArrayList<>());
        }
        composedSchema.getOneOf().addAll(composedFromSchema.getOneOf());
      }
    }
    if (fromSchema.getDiscriminator() != null) {
      if (schema.getDiscriminator() == null) {
        schema.setDiscriminator(new Discriminator());
      }
      final Discriminator discriminator = schema.getDiscriminator();
      final Discriminator fromDiscriminator = fromSchema.getDiscriminator();
      if (fromDiscriminator.getPropertyName() != null) {
        discriminator.setPropertyName(fromDiscriminator.getPropertyName());
      }
      if (fromDiscriminator.getMapping() != null) {
        if (discriminator.getMapping() == null) {
          discriminator.setMapping(new LinkedHashMap<>());
        }
        discriminator.getMapping().putAll(fromDiscriminator.getMapping());
      }
    }
    if (fromSchema.getTitle() != null) {
      schema.setTitle(fromSchema.getTitle());
    }
    if (fromSchema.getName() != null) {
      schema.setName(fromSchema.getName());
    }
    if (fromSchema.getAdditionalProperties() != null) {
      schema.setAdditionalProperties(fromSchema.getAdditionalProperties());
    }
    if (fromSchema.getDefault() != null) {
      schema.setDefault(fromSchema.getDefault());
    }
    if (fromSchema.getExample() != null) {
      schema.setExample(fromSchema.getExample());
    }
    if (fromSchema.getExternalDocs() != null) {
      if (schema.getExternalDocs() == null) {
        schema.setExternalDocs(new ExternalDocumentation());
      }
      final ExternalDocumentation externalDocs = schema.getExternalDocs();
      final ExternalDocumentation fromExternalDocs = fromSchema.getExternalDocs();
      if (fromExternalDocs.getDescription() != null) {
        externalDocs.setDescription(fromExternalDocs.getDescription());
      }
      if (fromExternalDocs.getExtensions() != null) {
        if (externalDocs.getExtensions() == null) {
          externalDocs.setExtensions(new LinkedHashMap<>());
        }
        externalDocs.getExtensions().putAll(fromExternalDocs.getExtensions());
      }
      if (fromExternalDocs.getUrl() != null) {
        externalDocs.setUrl(fromExternalDocs.getUrl());
      }
    }
    if (fromSchema.getMaximum() != null) {
      schema.setMaximum(fromSchema.getMaximum());
    }
    if (fromSchema.getMinimum() != null) {
      schema.setMinimum(fromSchema.getMinimum());
    }
    if (fromSchema.getMaxItems() != null) {
      schema.setMaxItems(fromSchema.getMaxItems());
    }
    if (fromSchema.getMinItems() != null) {
      schema.setMinItems(fromSchema.getMinItems());
    }
    if (fromSchema.getMaxProperties() != null) {
      schema.setMaxProperties(fromSchema.getMaxProperties());
    }
    if (fromSchema.getMinProperties() != null) {
      schema.setMinProperties(fromSchema.getMinProperties());
    }
    if (fromSchema.getMaxLength() != null) {
      schema.setMaxLength(fromSchema.getMaxLength());
    }
    if (fromSchema.getMinLength() != null) {
      schema.setMinLength(fromSchema.getMinLength());
    }
    if (fromSchema.getMultipleOf() != null) {
      schema.setMultipleOf(fromSchema.getMultipleOf());
    }
    if (fromSchema.getNot() != null) {
      if (schema.getNot() == null) {
        schema.setNot(addSchema(new Schema<>(), fromSchema.getNot()));
      } else {
        addSchema(schema.getNot(), fromSchema.getNot());
      }
    }
    if (fromSchema.getPattern() != null) {
      schema.setPattern(fromSchema.getPattern());
    }
    if (fromSchema.getXml() != null) {
      if (schema.getXml() == null) {
        schema.setXml(new XML());
      }
      final XML xml = schema.getXml();
      final XML fromXml = fromSchema.getXml();
      if (fromXml.getAttribute() != null) {
        xml.setAttribute(fromXml.getAttribute());
      }
      if (fromXml.getName() != null) {
        xml.setName(fromXml.getName());
      }
      if (fromXml.getNamespace() != null) {
        xml.setNamespace(fromXml.getNamespace());
      }
      if (fromXml.getExtensions() != null) {
        if (xml.getExtensions() == null) {
          xml.setExtensions(new LinkedHashMap<>());
        }
        xml.getExtensions().putAll(fromXml.getExtensions());
      }
      if (fromXml.getPrefix() != null) {
        xml.setPrefix(fromXml.getPrefix());
      }
      if (fromXml.getWrapped() != null) {
        xml.setWrapped(fromXml.getWrapped());
      }
    }
    return schema;
  }

  private static String getSchemaRef(Schema<?> schema) {
    return ofNullable(schema).map(Schema::get$ref).orElse(null);
  }

  public DeferredChanged<ChangedSchema> diff(Schema left, Schema right, DiffContext context) {
    return this.diff(new RecursiveSchemaSet(), left, right, context);
  }

  public DeferredChanged<ChangedSchema> diff(
      RecursiveSchemaSet refSet, Schema left, Schema right, DiffContext context) {
    if (left == null && right == null) {
      return new RealizedChanged<>(Optional.empty());
    }
    return computeDeferredDiff(refSet, left, right, context);
  }

  public DeferredChanged<ChangedSchema> getTypeChangedSchema(
      Schema left, Schema right, DiffContext context) {
    return new RealizedChanged(
        SchemaDiff.getSchemaDiffResult(openApiDiff)
            .getChangedSchema()
            .setOldSchema(left)
            .setNewSchema(right)
            .setChangedType(true)
            .setContext(context));
  }

  protected DeferredChanged<ChangedSchema> computeDeferredDiff(
      RecursiveSchemaSet refSet, Schema<?> left, Schema<?> right, DiffContext context) {

    CacheKey key = new CacheKey(getSchemaRef(left), getSchemaRef(right), context);
    if (key.getLeft() != null && key.getRight() != null) {
      return openApiDiff.getDeferredSchemaCache().getOrAddSchema(refSet, key, left, right);
    } else {
      return computeDiffForReal(refSet, left, right, context);
    }
  }

  public DeferredChanged<ChangedSchema> computeDiffForReal(
      RecursiveSchemaSet refSet, Schema left, Schema right, DiffContext context) {

    left = refPointer.resolveRef(this.leftComponents, left, getSchemaRef(left));
    right = refPointer.resolveRef(this.rightComponents, right, getSchemaRef(right));

    left = resolveComposedSchema(leftComponents, left, new HashSet<>());
    right = resolveComposedSchema(rightComponents, right, new HashSet<>());

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
