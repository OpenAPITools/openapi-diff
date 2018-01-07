package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedContent;
import com.qdesrame.openapi.diff.model.ChangedMediaType;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ContentDiff implements Comparable<Content> {

    private OpenApiDiff openApiDiff;

    public ContentDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
    }

    @Override
    public boolean compare(Content left, Content right) {
        return false;
    }

    public Optional<ChangedContent> diff(Content left, Content right) {
        ChangedContent changedContent = new ChangedContent(left, right);

        MapKeyDiff<String, MediaType> mediaTypeDiff = MapKeyDiff.diff(left, right);
        changedContent.setIncreased(mediaTypeDiff.getIncreased());
        changedContent.setMissing(mediaTypeDiff.getMissing());
        List<String> sharedMediaTypes = mediaTypeDiff.getSharedKey();
        Map<String, ChangedMediaType> changedMediaTypes = new HashMap<>();
        for (String mediaTypeKey : sharedMediaTypes) {
            MediaType oldMediaType = left.get(mediaTypeKey);
            MediaType newMediaType = right.get(mediaTypeKey);
            ChangedMediaType changedMediaType = new ChangedMediaType(oldMediaType.getSchema(), newMediaType.getSchema());
            openApiDiff.getSchemaDiff().diff(oldMediaType.getSchema(), newMediaType.getSchema()).ifPresent(changedMediaType::setChangedSchema);
            if (changedMediaType.isDiff()) {
                changedMediaTypes.put(mediaTypeKey, changedMediaType);
            }
        }
        changedContent.setChanged(changedMediaTypes);
        return changedContent.isDiff() ? Optional.of(changedContent) : Optional.empty();
    }
}
