package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangedMediaType implements ComposedChanged {

  private final Schema oldSchema;
  private final Schema newSchema;
  private final DiffContext context;
  private ChangedSchema schema;

  public ChangedMediaType(Schema oldSchema, Schema newSchema, DiffContext context) {
    this.oldSchema = oldSchema;
    this.newSchema = newSchema;
    this.context = context;
  }

  @Override
  public List<Changed> getChangedElements() {
    return Collections.singletonList(schema);
  }

  @Override
  public DiffResult isCoreChanged() {
    return DiffResult.NO_CHANGES;
  }
}
