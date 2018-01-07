package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedHeader;
import com.qdesrame.openapi.diff.utils.RefPointer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.headers.Header;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
public class HeaderDiff extends ReferenceDiffCache<Header, ChangedHeader> {
    private OpenApiDiff openApiDiff;
    private Components leftComponents;
    private Components rightComponents;

    public HeaderDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.leftComponents = openApiDiff.getOldSpecOpenApi() != null ? openApiDiff.getOldSpecOpenApi().getComponents() : null;
        this.rightComponents = openApiDiff.getNewSpecOpenApi() != null ? openApiDiff.getNewSpecOpenApi().getComponents() : null;
    }

    public Optional<ChangedHeader> diff(Header left, Header right) {
        return super.cachedDiff(left, right, left.get$ref(), right.get$ref());
    }

    @Override
    protected Optional<ChangedHeader> computeDiff(Header left, Header right) {
        left = RefPointer.Replace.header(leftComponents, left);
        right = RefPointer.Replace.header(rightComponents, right);

        ChangedHeader changedHeader = new ChangedHeader(left, right);

        changedHeader.setChangeDescription(!Objects.equals(left.getDescription(), right.getDescription()));
        changedHeader.setChangeRequired(getBooleanDiff(left.getRequired(), right.getRequired()));
        changedHeader.setChangeDeprecated(!Boolean.TRUE.equals(left.getDeprecated()) && Boolean.TRUE.equals(right.getDeprecated()));
        changedHeader.setChangeAllowEmptyValue(getBooleanDiff(left.getAllowEmptyValue(), right.getAllowEmptyValue()));
        changedHeader.setChangeStyle(!Objects.equals(left.getStyle(), right.getStyle()));
        changedHeader.setChangeExplode(getBooleanDiff(left.getExplode(), right.getExplode()));
        openApiDiff.getSchemaDiff().diff(left.getSchema(), right.getSchema()).ifPresent(changedHeader::setChangedSchema);
        openApiDiff.getContentDiff().diff(left.getContent(), right.getContent()).ifPresent(changedHeader::setChangedContent);

        return changedHeader.isDiff() ? Optional.of(changedHeader) : Optional.empty();
    }

    private boolean getBooleanDiff(Boolean left, Boolean right) {
        boolean leftRequired = Optional.ofNullable(left).orElse(Boolean.FALSE);
        boolean rightRequired = Optional.ofNullable(right).orElse(Boolean.FALSE);
        return leftRequired != rightRequired;
    }
}
