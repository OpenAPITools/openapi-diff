package org.openapitools.openapidiff.core.output;

import java.io.IOException;
import java.io.OutputStreamWriter;
import org.openapitools.openapidiff.core.exception.RendererException;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public interface Render {

  void render(ChangedOpenApi diff, OutputStreamWriter outputStreamWriter);

  default void safelyAppend(OutputStreamWriter outputStreamWriter, String csq) {
    try {
      outputStreamWriter.append(csq);
    } catch (IOException ex) {
      throw new RendererException(ex);
    }
  }
}
