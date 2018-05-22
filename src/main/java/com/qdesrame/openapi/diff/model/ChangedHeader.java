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
    private final Header oldHeader;
    private final Header newHeader;
    private final DiffContext context;

    private boolean changeDescription;
    private boolean changeRequired;
    private boolean changeDeprecated;
    private boolean changeStyle;
    private boolean changeExplode;
    private ChangedSchema changedSchema;
    private ChangedContent changedContent;

    public ChangedHeader(Header oldHeader, Header newHeader, DiffContext context) {
        this.oldHeader = oldHeader;
        this.newHeader = newHeader;
        this.context = context;
    }

    @Override
    public DiffResult isChanged() {
        if (!changeDescription && !changeRequired && !changeDeprecated && !changeStyle && !changeExplode
                && (changedSchema == null || changedSchema.isUnchanged())
                && (changedContent == null || changedContent.isUnchanged())) {
            return DiffResult.NO_CHANGES;
        }
        if (!changeRequired && !changeStyle && !changeExplode
                && (changedSchema == null || changedSchema.isCompatible())
                && (changedContent == null || changedContent.isCompatible())) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }

}
