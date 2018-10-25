package com.qdesrame.openapi.diff.compare;

import static com.qdesrame.openapi.diff.utils.ChangedUtils.isChanged;
import static com.qdesrame.openapi.diff.utils.ChangedUtils.isUnchanged;

import com.qdesrame.openapi.diff.model.ChangedContent;
import com.qdesrame.openapi.diff.model.ChangedMediaType;
import com.qdesrame.openapi.diff.model.DiffContext;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import java.util.*;

public class ContentDiff implements Comparable<Content> {

  private OpenApiDiff openApiDiff;

  public ContentDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  @Override
  public boolean compare(Content left, Content right) {
    return false;
  }

  public Optional<ChangedContent> diff(Content left, Content right, DiffContext context) {
    ChangedContent changedContent = new ChangedContent(left, right, context);

    MapKeyDiff<String, MediaType> mediaTypeDiff = MapKeyDiff.diff(left, right);
    changedContent.setIncreased(mediaTypeDiff.getIncreased());
    changedContent.setMissing(mediaTypeDiff.getMissing());
    List<String> sharedMediaTypes = mediaTypeDiff.getSharedKey();
    Map<String, ChangedMediaType> changedMediaTypes = new LinkedHashMap<>();
    for (String mediaTypeKey : sharedMediaTypes) {
      MediaType oldMediaType = left.get(mediaTypeKey);
      MediaType newMediaType = right.get(mediaTypeKey);
      ChangedMediaType changedMediaType =
          new ChangedMediaType(oldMediaType.getSchema(), newMediaType.getSchema(), context);
      openApiDiff
          .getSchemaDiff()
          .diff(
              new HashSet<>(),
              oldMediaType.getSchema(),
              newMediaType.getSchema(),
              context.copyWithRequired(true))
          .ifPresent(changedMediaType::setChangedSchema);
      if (!isUnchanged(changedMediaType)) {
        changedMediaTypes.put(mediaTypeKey, changedMediaType);
      }
    }
    changedContent.setChanged(changedMediaTypes);
    return isChanged(changedContent);
  }
}
