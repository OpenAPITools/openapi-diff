package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.headers.Header;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
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

    public Header getOldHeader() {
        return oldHeader;
    }

    public void setOldHeader(Header oldHeader) {
        this.oldHeader = oldHeader;
    }

    public Header getNewHeader() {
        return newHeader;
    }

    public void setNewHeader(Header newHeader) {
        this.newHeader = newHeader;
    }

    public boolean isChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(boolean changeDescription) {
        this.changeDescription = changeDescription;
    }

    public boolean isChangeRequired() {
        return changeRequired;
    }

    public void setChangeRequired(boolean changeRequired) {
        this.changeRequired = changeRequired;
    }

    public boolean isChangeDeprecated() {
        return changeDeprecated;
    }

    public void setChangeDeprecated(boolean changeDeprecated) {
        this.changeDeprecated = changeDeprecated;
    }

    public boolean isChangeAllowEmptyValue() {
        return changeAllowEmptyValue;
    }

    public void setChangeAllowEmptyValue(boolean changeAllowEmptyValue) {
        this.changeAllowEmptyValue = changeAllowEmptyValue;
    }

    public boolean isChangeStyle() {
        return changeStyle;
    }

    public void setChangeStyle(boolean changeStyle) {
        this.changeStyle = changeStyle;
    }

    public boolean isChangeExplode() {
        return changeExplode;
    }

    public void setChangeExplode(boolean changeExplode) {
        this.changeExplode = changeExplode;
    }

    public ChangedSchema getChangedSchema() {
        return changedSchema;
    }

    public void setChangedSchema(ChangedSchema changedSchema) {
        this.changedSchema = changedSchema;
    }

    public ChangedContent getChangedContent() {
        return changedContent;
    }

    public void setChangedContent(ChangedContent changedContent) {
        this.changedContent = changedContent;
    }

}
