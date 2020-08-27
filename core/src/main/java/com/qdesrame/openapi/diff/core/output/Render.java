package com.qdesrame.openapi.diff.core.output;

import com.qdesrame.openapi.diff.core.model.ChangedOpenApi;

public interface Render {

  String render(ChangedOpenApi diff);
}
