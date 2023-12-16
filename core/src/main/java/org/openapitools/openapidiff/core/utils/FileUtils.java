package org.openapitools.openapidiff.core.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.output.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileUtils {
  private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

  private FileUtils() {
    throw new UnsupportedOperationException("Utility class. Do not instantiate");
  }

  public static void writeToFile(
      final Render render, final ChangedOpenApi diff, final String fileName) {
    if (fileName == null || fileName.isEmpty()) {
      logger.debug("File name cannot be null or empty.");
      return;
    }

    final Path filePath = Paths.get(fileName);
    try (final FileOutputStream outputStream = new FileOutputStream(filePath.toFile());
        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
      render.render(diff, outputStreamWriter);
    } catch (final IOException e) {
      logger.error("Exception while writing to file {}", fileName, e);
    }
  }
}
