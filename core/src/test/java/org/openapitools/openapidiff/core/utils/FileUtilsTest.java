package org.openapitools.openapidiff.core.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FileUtilsTest {

  private static final Logger logger = LoggerFactory.getLogger(FileUtilsTest.class);

  private final String filePath = "target/output.txt";
  private final String sampleContent = "someContent";

  @BeforeEach
  void setup() {
    cleanupGeneratedFiles();
  }

  @AfterEach
  void tearDown() {
    cleanupGeneratedFiles();
  }

  @Test
  void isValidFileName_fileNameIsValid_returnsTrue() {
    assertTrue(FileUtils.isValidFileName(filePath));
  }

  @Test
  void isValidFileName_fileNameIsNull_returnsFalse() {
    assertFalse(FileUtils.isValidFileName(null));
  }

  @Test
  void isValidFileName_fileNameIsEmpty_returnsFalse() {
    final String filename = StringUtils.EMPTY;
    assertFalse(FileUtils.isValidFileName(filename));
  }

  @Test
  void writeToFile_contentIsNull_doesNothing() {
    assertDoesNotThrow(() -> FileUtils.writeToFile(null, filePath));
    assertFalse(Files.exists(Paths.get(filePath)));
  }

  @Test
  void writeToFile_filenameIsNull_doesNothing() {
    assertDoesNotThrow(() -> FileUtils.writeToFile(sampleContent, null));
    assertFalse(Files.exists(Paths.get(filePath)));
  }

  @Test
  void writeToFile_filenameIsEmpty_doesNothing() {
    assertDoesNotThrow(() -> FileUtils.writeToFile(sampleContent, StringUtils.EMPTY));
    assertFalse(Files.exists(Paths.get(filePath)));
  }

  @Test
  void writeToFile_fileExists_doesNothing() {
    final Path path = Paths.get(filePath);
    try {
      Files.createFile(path);
    } catch (IOException e) {
      fail("Cannot create file for test.");
    }
    assertDoesNotThrow(() -> FileUtils.writeToFile(sampleContent, filePath));
    assertTrue(Files.exists(path));
  }

  @Test
  void writeToFile_fileDoesNotExist_createsFile() {
    assertDoesNotThrow(() -> FileUtils.writeToFile(sampleContent, filePath));
    assertTrue(Files.exists(Paths.get(filePath)));
  }

  private void cleanupGeneratedFiles() {
    try {
      Files.deleteIfExists(Paths.get(filePath));
    } catch (IOException ioException) {
      logger.warn("Exception while trying to delete file {}", filePath);
    }
  }
}
