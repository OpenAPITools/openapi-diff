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
public class HeaderDiff {
    private OpenApiDiff openApiDiff;
    private Components leftComponents;
    private Components rightComponents;
    private ReferenceDiffCache<ChangedHeader> headerReferenceDiffCache;

    public HeaderDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.leftComponents = openApiDiff.getOldSpecOpenApi() != null ? openApiDiff.getOldSpecOpenApi().getComponents() : null;
        this.rightComponents = openApiDiff.getNewSpecOpenApi() != null ? openApiDiff.getNewSpecOpenApi().getComponents() : null;
        this.headerReferenceDiffCache = new ReferenceDiffCache<>();
    }

    public ChangedHeader diff(Header left, Header right) {
        String leftRef = left.get$ref();
        String rightRef = right.get$ref();
        boolean areBothRefHeaders = leftRef != null && rightRef != null;
        if (areBothRefHeaders) {
            ChangedHeader changedHeaderFromCache = headerReferenceDiffCache.getFromCache(leftRef, rightRef);
            if (changedHeaderFromCache != null) {
                return changedHeaderFromCache;
            }
        }

        left = RefPointer.Replace.header(leftComponents, left);
        right = RefPointer.Replace.header(rightComponents, right);

        ChangedHeader changedHeader = new ChangedHeader(left, right);

        changedHeader.setChangeDescription(!Objects.equals(left.getDescription(), right.getDescription()));
        changedHeader.setChangeRequired(getBooleanDiff(left.getRequired(), right.getRequired()));
        changedHeader.setChangeDeprecated(!Boolean.TRUE.equals(left.getDeprecated()) && Boolean.TRUE.equals(right.getDeprecated()));
        changedHeader.setChangeAllowEmptyValue(getBooleanDiff(left.getAllowEmptyValue(), right.getAllowEmptyValue()));
        changedHeader.setChangeStyle(!Objects.equals(left.getStyle(), right.getStyle()));
        changedHeader.setChangeExplode(getBooleanDiff(left.getExplode(), right.getExplode()));
        changedHeader.setChangedSchema(openApiDiff.getSchemaDiff().diff(left.getSchema(), right.getSchema()));
        changedHeader.setChangedContent(openApiDiff.getContentDiff().diff(left.getContent(), right.getContent()));

        if (areBothRefHeaders) {
            headerReferenceDiffCache.addToCache(leftRef, rightRef, changedHeader);
        }

        return changedHeader.isDiff() ? changedHeader : null;
    }

    private boolean getBooleanDiff(Boolean left, Boolean right) {
        boolean leftRequired = Optional.ofNullable(left).orElse(Boolean.FALSE);
        boolean rightRequired = Optional.ofNullable(right).orElse(Boolean.FALSE);
        return leftRequired != rightRequired;
    }
}
