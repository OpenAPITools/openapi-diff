package org.openapitools.openapidiff.core.compare;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.headers.Header;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedExample;
import org.openapitools.openapidiff.core.model.ChangedExamples;
import org.openapitools.openapidiff.core.model.ChangedHeader;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.deferred.DeferredBuilder;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;
import org.openapitools.openapidiff.core.utils.RefPointer;
import org.openapitools.openapidiff.core.utils.RefType;

public class HeaderDiff extends ReferenceDiffCache<Header, ChangedHeader> {
  private static final RefPointer<Header> refPointer = new RefPointer<>(RefType.HEADERS);
  private final OpenApiDiff openApiDiff;
  private final Components leftComponents;
  private final Components rightComponents;

  public HeaderDiff(OpenApiDiff openApiDiff) {
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

  public DeferredChanged<ChangedHeader> diff(Header left, Header right, DiffContext context) {
    return cachedDiff(new HashSet<>(), left, right, left.get$ref(), right.get$ref(), context);
  }

  @Override
  protected DeferredChanged<ChangedHeader> computeDiff(
      HashSet<String> refSet, Header left, Header right, DiffContext context) {
    left = refPointer.resolveRef(leftComponents, left, left.get$ref());
    right = refPointer.resolveRef(rightComponents, right, right.get$ref());

    DeferredBuilder<Changed> builder = new DeferredBuilder<>();
    ChangedHeader changedHeader =
        new ChangedHeader(left, right, context)
            .setRequired(getBooleanDiff(left.getRequired(), right.getRequired()))
            .setDeprecated(!Objects.equals(left.getDeprecated(), right.getDeprecated()))
            .setStyle(!Objects.equals(left.getStyle(), right.getStyle()))
            .setExplode(getBooleanDiff(left.getExplode(), right.getExplode()))
            .setExamples(new ChangedExamples(left.getExamples(), right.getExamples()))
            .setExample(new ChangedExample(left.getExample(), right.getExample()));
    builder
        .with(
            openApiDiff
                .getMetadataDiff()
                .diff(left.getDescription(), right.getDescription(), context))
        .ifPresent(changedHeader::setDescription);
    builder
        .with(
            openApiDiff
                .getSchemaDiff()
                .diff(left.getSchema(), right.getSchema(), context.copyWithRequired(true)))
        .ifPresent(changedHeader::setSchema);
    builder
        .with(openApiDiff.getContentDiff().diff(left.getContent(), right.getContent(), context))
        .ifPresent(changedHeader::setContent);
    builder
        .with(
            openApiDiff
                .getExtensionsDiff()
                .diff(left.getExtensions(), right.getExtensions(), context))
        .ifPresent(changedHeader::setExtensions);
    return builder.buildIsChanged(changedHeader);
  }

  private boolean getBooleanDiff(Boolean left, Boolean right) {
    boolean leftRequired = Optional.ofNullable(left).orElse(Boolean.FALSE);
    boolean rightRequired = Optional.ofNullable(right).orElse(Boolean.FALSE);
    return leftRequired != rightRequired;
  }
}
