package com.qdesrame.openapi.diff.output;

import com.qdesrame.openapi.diff.model.ChangedOpenApi;

public interface Render {

  String render(ChangedOpenApi diff);
}
