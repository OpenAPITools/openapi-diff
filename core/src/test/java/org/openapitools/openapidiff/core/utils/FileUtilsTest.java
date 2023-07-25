package org.openapitools.openapidiff.core.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.output.ConsoleRender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FileUtilsTest {

  private static final Logger logger = LoggerFactory.getLogger(FileUtilsTest.class);

  private ChangedOpenApi changedOpenApi;

  @BeforeEach
  void setup() {
    changedOpenApi = new ChangedOpenApi();
    changedOpenApi.setChangedSchemas(Collections.emptyList());
    changedOpenApi.setChangedOperations(Collections.emptyList());
    changedOpenApi.setNewEndpoints(Collections.emptyList());
    changedOpenApi.setMissingEndpoints(Collections.emptyList());
  }

  @Test
  void writeToFile_contentIsNull_doesNothing(@TempDir Path tempDir) throws IOException {
    Path path = tempDir.resolve("output.txt");
    Files.write(path, "Test".getBytes(StandardCharsets.UTF_8));

    assertDoesNotThrow(() -> FileUtils.writeToFile(null, changedOpenApi, path.toString()));
    assertThat(path).exists().content().isEqualTo("Test");
  }

  @Test
  void writeToFile_filenameIsNull_doesNothing() {
    assertDoesNotThrow(() -> FileUtils.writeToFile(new ConsoleRender(), changedOpenApi, null));
  }

  @Test
  void writeToFile_filenameIsEmpty_doesNothing() {
    assertDoesNotThrow(
        () -> FileUtils.writeToFile(new ConsoleRender(), changedOpenApi, StringUtils.EMPTY));
  }

  @Test
  void writeToFile_fileExists_doesNothing(@TempDir Path tempDir) throws IOException {
    final Path path = tempDir.resolve("output.txt");
    Files.write(path, "Test".getBytes(StandardCharsets.UTF_8));

    assertDoesNotThrow(
        () -> FileUtils.writeToFile(new ConsoleRender(), changedOpenApi, path.toString()));
    assertThat(path).exists().content().isEqualTo("Test");
  }

  @Test
  void writeToFile_fileDoesNotExist_createsFile(@TempDir Path tempDir) {
    final Path path = tempDir.resolve("output.txt");
    assertDoesNotThrow(
        () -> FileUtils.writeToFile(new ConsoleRender(), changedOpenApi, path.toString()));
    assertThat(path).exists();
  }
}
