package org.openapitools.openapidiff.core.output;

import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public interface Render {

  String render(ChangedOpenApi diff);
}
