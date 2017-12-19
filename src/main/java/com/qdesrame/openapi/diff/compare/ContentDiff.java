package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.compare.schemaDiffResult.SchemaDiffResult;
import com.qdesrame.openapi.diff.model.ChangedMediaType;
import io.swagger.oas.models.Components;
import io.swagger.oas.models.media.Content;
import io.swagger.oas.models.media.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentDiff implements Comparable<Content> {

    private Components leftComponents;
    private Components rightComponents;

    private ContentDiff(Components left, Components right) {
        this.leftComponents = left;
        this.rightComponents = right;
    }

    public static ContentDiff fromComponents(Components left, Components right) {
        return new ContentDiff(left, right);
    }

    @Override
    public boolean compare(Content left, Content right) {

        return false;
    }

    public ContentDiffResult diff(Content left, Content right) {
        ContentDiffResult result = new ContentDiffResult();

        MapKeyDiff<String, MediaType> mediaTypeDiff = MapKeyDiff.diff(left, right);
        result.setIncreased(mediaTypeDiff.getIncreased());
        result.setMissing(mediaTypeDiff.getMissing());
        List<String> sharedMediaTypes = mediaTypeDiff.getSharedKey();
        Map<String, ChangedMediaType> changedMediaTypes = new HashMap<>();
        ChangedMediaType changedMediaType;
        for (String mediaTypeKey : sharedMediaTypes) {
            changedMediaType = new ChangedMediaType();
            MediaType oldMediaType = left.get(mediaTypeKey);
            MediaType newMediaType = right.get(mediaTypeKey);
            SchemaDiffResult schemaDiff = SchemaDiff.fromComponents(leftComponents, rightComponents)
                    .diff(oldMediaType.getSchema(), newMediaType.getSchema());
            changedMediaType.setSchema(schemaDiff);
            if (changedMediaType.isDiff()) {
                changedMediaTypes.put(mediaTypeKey, changedMediaType);
            }
        }
        result.setChanged(changedMediaTypes);
        return result;
    }
}
