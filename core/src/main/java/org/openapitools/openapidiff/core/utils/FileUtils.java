package org.openapitools.openapidiff.core.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileUtils {

  private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

  private FileUtils() {
    throw new UnsupportedOperationException("Utility class. Do not instantiate");
  }

  public static boolean isValidFileName(final String fileName) {
    return fileName != null && !fileName.isEmpty();
  }

  public static void writeToFile(final String content, final String fileName) {
    if (content == null) {
      logger.debug("Content cannot be null");
      return;
    }

    if (!isValidFileName(fileName)) {
      logger.debug("File name cannot be null or empty.");
      return;
    }

    try {
      final Path filePath = Paths.get(fileName);
      if (Files.exists(filePath)) {
        logger.info("File {} already exists. Skip writing to file.", fileName);
        return;
      }
    } catch (final InvalidPathException invalidPathException) {
      throw new IllegalArgumentException("File name is an invalid path.");
    }

    try (final FileWriter fileWriter = new FileWriter(fileName)) {
      fileWriter.write(content);
    } catch (final IOException ioException) {
      logger.error("Exception while writing to file {}", fileName);
    }
  }
}
