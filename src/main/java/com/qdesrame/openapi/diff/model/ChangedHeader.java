package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.headers.Header;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
@Getter
@Setter
public class ChangedHeader implements Changed {
    private Header oldHeader;
    private Header newHeader;

    private boolean changeDescription;
    private boolean changeRequired;
    private boolean changeDeprecated;
    private boolean changeAllowEmptyValue;
    private boolean changeStyle;
    private boolean changeExplode;
    private ChangedSchema changedSchema;
    private ChangedContent changedContent;

    public ChangedHeader(Header oldHeader, Header newHeader) {
        this.oldHeader = oldHeader;
        this.newHeader = newHeader;
    }

    @Override
    public boolean isDiff() {
        return changeDescription
                || changeRequired
                || changeDeprecated
                || changeAllowEmptyValue
                || changeStyle
                || changeExplode
                || (changedSchema != null && changedSchema.isDiff())
                || (changedContent != null && changedContent.isDiff());
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return changeRequired
                && changeAllowEmptyValue
                && changeStyle
                && changeExplode
                && (changedSchema == null || changedSchema.isDiffBackwardCompatible(false))
                && (changedContent == null || changedContent.isDiffBackwardCompatible(false));
    }
}
