package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.headers.Header;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import org.openapitools.openapidiff.core.model.ChangedHeader;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.utils.RefPointer;
import org.openapitools.openapidiff.core.utils.RefType;

/** Created by adarsh.sharma on 28/12/17. */
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

  public Optional<ChangedHeader> diff(Header left, Header right, DiffContext context) {
    return cachedDiff(new HashSet<>(), left, right, left.get$ref(), right.get$ref(), context);
  }

  @Override
  protected Optional<ChangedHeader> computeDiff(
      HashSet<String> refSet, Header left, Header right, DiffContext context) {
    left = refPointer.resolveRef(leftComponents, left, left.get$ref());
    right = refPointer.resolveRef(rightComponents, right, right.get$ref());

    ChangedHeader changedHeader =
        new ChangedHeader(left, right, context)
            .setRequired(getBooleanDiff(left.getRequired(), right.getRequired()))
            .setDeprecated(
                !Boolean.TRUE.equals(left.getDeprecated())
                    && Boolean.TRUE.equals(right.getDeprecated()))
            .setStyle(!Objects.equals(left.getStyle(), right.getStyle()))
            .setExplode(getBooleanDiff(left.getExplode(), right.getExplode()));
    openApiDiff
        .getMetadataDiff()
        .diff(left.getDescription(), right.getDescription(), context)
        .ifPresent(changedHeader::setDescription);
    openApiDiff
        .getSchemaDiff()
        .diff(new HashSet<>(), left.getSchema(), right.getSchema(), context.copyWithRequired(true))
        .ifPresent(changedHeader::setSchema);
    openApiDiff
        .getContentDiff()
        .diff(left.getContent(), right.getContent(), context)
        .ifPresent(changedHeader::setContent);
    openApiDiff
        .getExtensionsDiff()
        .diff(left.getExtensions(), right.getExtensions(), context)
        .ifPresent(changedHeader::setExtensions);
    return isChanged(changedHeader);
  }

  private boolean getBooleanDiff(Boolean left, Boolean right) {
    boolean leftRequired = Optional.ofNullable(left).orElse(Boolean.FALSE);
    boolean rightRequired = Optional.ofNullable(right).orElse(Boolean.FALSE);
    return leftRequired != rightRequired;
  }
}
