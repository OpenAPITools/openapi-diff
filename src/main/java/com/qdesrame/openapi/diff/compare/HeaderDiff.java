package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedHeader;
import io.swagger.v3.oas.models.headers.Header;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
public class HeaderDiff {
    private OpenApiDiff openApiDiff;

    public HeaderDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
    }

    public ChangedHeader diff(Header left, Header right) {
        ChangedHeader changedHeader = new ChangedHeader(left, right);

        changedHeader.setChangeDescription(!Objects.equals(left.getDescription(), right.getDescription()));
        changedHeader.setChangeRequired(getBooleanDiff(left.getRequired(), right.getRequired()));
        changedHeader.setChangeDeprecated(!Boolean.TRUE.equals(left.getDeprecated()) && Boolean.TRUE.equals(right.getDeprecated()));
        changedHeader.setChangeAllowEmptyValue(getBooleanDiff(left.getAllowEmptyValue(), right.getAllowEmptyValue()));
        changedHeader.setChangeStyle(!Objects.equals(left.getStyle(), right.getStyle()));
        changedHeader.setChangeExplode(getBooleanDiff(left.getExplode(), right.getExplode()));
        changedHeader.setChangedSchema(openApiDiff.getSchemaDiff().diff(left.getSchema(), right.getSchema()));
        changedHeader.setChangedContent(openApiDiff.getContentDiff().diff(left.getContent(), right.getContent()));

        return changedHeader;
    }

    private boolean getBooleanDiff(Boolean left, Boolean right) {
        boolean leftRequired = Optional.ofNullable(left).orElse(Boolean.FALSE);
        boolean rightRequired = Optional.ofNullable(right).orElse(Boolean.FALSE);
        return leftRequired != rightRequired;
    }
}
