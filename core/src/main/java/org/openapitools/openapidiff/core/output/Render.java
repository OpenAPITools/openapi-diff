package org.openapitools.openapidiff.core.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.openapitools.openapidiff.core.exception.RendererException;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public interface Render {

  /**
   * render provided diff object
   *
   * @param diff diff object to render
   * @param outputStreamWriter writer for rendered results
   */
  void render(ChangedOpenApi diff, OutputStreamWriter outputStreamWriter) throws RendererException;

  /**
   * render provided diff object
   *
   * @deprecated since 2.1.0, use {@link Render#render(ChangedOpenApi, OutputStreamWriter)} to avoid
   *     massive String output issues. details <a
   *     href="https://github.com/OpenAPITools/openapi-diff/issues/543">#543</a>
   * @param diff diff object to render
   * @return rendered output
   */
  @Deprecated
  default String render(ChangedOpenApi diff) throws RendererException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
    render(diff, outputStreamWriter);
    String result = byteArrayOutputStream.toString();

    try {
      outputStreamWriter.close();
    } catch (IOException e) {
      throw new RendererException(e);
    }

    return result;
  }

  default void safelyAppend(OutputStreamWriter outputStreamWriter, String csq) {
    try {
      outputStreamWriter.append(csq);
    } catch (IOException ex) {
      throw new RendererException(ex);
    }
  }
}
