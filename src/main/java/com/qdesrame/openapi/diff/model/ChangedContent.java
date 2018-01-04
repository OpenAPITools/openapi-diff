package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adarsh.sharma on 22/12/17.
 */
@Getter
@Setter
public class ChangedContent implements RequestResponseChanged {
    private Content oldContent;
    private Content newContent;

    private Map<String, MediaType> increased;
    private Map<String, MediaType> missing;
    private Map<String, ChangedMediaType> changed;

    public ChangedContent(Content oldContent, Content newContent) {
        this.oldContent = oldContent;
        this.newContent = newContent;
        this.increased = new HashMap<>();
        this.missing = new HashMap<>();
        this.changed = new HashMap<>();
    }

    @Override
    public boolean isDiff() {
        return !increased.isEmpty() || !missing.isEmpty() || !changed.isEmpty();
    }

    @Override
    public boolean isDiffBackwardCompatible(boolean isRequest) {
        return ((isRequest && missing.isEmpty()) || (!isRequest && increased.isEmpty()))
                && changed.values().stream().allMatch(c -> c.isDiffBackwardCompatible(isRequest));
    }

}
