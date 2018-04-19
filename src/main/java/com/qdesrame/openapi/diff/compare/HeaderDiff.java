package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedHeader;
import com.qdesrame.openapi.diff.model.DiffContext;
import com.qdesrame.openapi.diff.utils.RefPointer;
import com.qdesrame.openapi.diff.utils.RefType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.headers.Header;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

import static com.qdesrame.openapi.diff.utils.ChangedUtils.isChanged;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
public class HeaderDiff extends ReferenceDiffCache<Header, ChangedHeader> {
    private OpenApiDiff openApiDiff;
    private Components leftComponents;
    private Components rightComponents;
    private static RefPointer<Header> refPointer = new RefPointer<>(RefType.HEADERS);

    public HeaderDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.leftComponents = openApiDiff.getOldSpecOpenApi() != null ? openApiDiff.getOldSpecOpenApi().getComponents() : null;
        this.rightComponents = openApiDiff.getNewSpecOpenApi() != null ? openApiDiff.getNewSpecOpenApi().getComponents() : null;
    }

    public Optional<ChangedHeader> diff(Header left, Header right, DiffContext context) {
        return cachedDiff(new HashSet<>(), left, right, left.get$ref(), right.get$ref(), context);
    }

    @Override
    protected Optional<ChangedHeader> computeDiff(HashSet<String> refSet, Header left, Header right, DiffContext context) {
        left = refPointer.resolveRef(leftComponents, left, left.get$ref());
        right = refPointer.resolveRef(rightComponents, right, right.get$ref());

        ChangedHeader changedHeader = new ChangedHeader(left, right, context);

        changedHeader.setChangeDescription(!Objects.equals(left.getDescription(), right.getDescription()));
        changedHeader.setChangeRequired(getBooleanDiff(left.getRequired(), right.getRequired()));
        changedHeader.setChangeDeprecated(!Boolean.TRUE.equals(left.getDeprecated()) && Boolean.TRUE.equals(right.getDeprecated()));
        changedHeader.setChangeStyle(!Objects.equals(left.getStyle(), right.getStyle()));
        changedHeader.setChangeExplode(getBooleanDiff(left.getExplode(), right.getExplode()));
        openApiDiff.getSchemaDiff().diff(new HashSet<>(), left.getSchema(), right.getSchema(), context).ifPresent(changedHeader::setChangedSchema);
        openApiDiff.getContentDiff().diff(left.getContent(), right.getContent(), context).ifPresent(changedHeader::setChangedContent);

        return isChanged(changedHeader);
    }

    private boolean getBooleanDiff(Boolean left, Boolean right) {
        boolean leftRequired = Optional.ofNullable(left).orElse(Boolean.FALSE);
        boolean rightRequired = Optional.ofNullable(right).orElse(Boolean.FALSE);
        return leftRequired != rightRequired;
    }
}
