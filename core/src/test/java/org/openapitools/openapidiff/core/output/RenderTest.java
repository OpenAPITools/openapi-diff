package org.openapitools.openapidiff.core.output;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.exception.RendererException;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

class RenderTest {

  private final Render testRenderImpl =
      (diff, outputStreamWriter) -> {
        try {
          outputStreamWriter.write("Output");
          outputStreamWriter.close();
        } catch (IOException e) {
          throw new RendererException(e);
        }
      };

  @Test
  void testDefaultRenderMethod() {
    ChangedOpenApi diff = new ChangedOpenApi(null);
    assertThat(testRenderImpl.render(diff)).isEqualTo("Output");
  }
}
