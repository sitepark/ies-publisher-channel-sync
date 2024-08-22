package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.sitepark.ies.publisher.channel.sync.port.Publisher;
import com.sitepark.ies.publisher.channel.sync.port.SyncNotifier;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Comparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
class SyncronizeContextTest {

  private final Path workDir = Path.of("target/test/SyncronizeContextTest");

  @BeforeEach
  public void setup() throws IOException {
    this.deleteWorkDir();
    Files.createDirectories(this.workDir);
  }

  private void deleteWorkDir() throws IOException {
    if (!Files.isDirectory(this.workDir)) {
      return;
    }
    Files.walk(this.workDir)
        .forEach(
            path -> {
              try {
                Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxrwxrwx"));
              } catch (IOException e) {
                throw new UncheckedIOException(e);
              }
            });
    Files.walk(this.workDir)
        .sorted(Comparator.reverseOrder())
        .forEach(
            path -> {
              try {
                Files.delete(path);
              } catch (IOException e) {
                throw new UncheckedIOException(e);
              }
            });
  }

  @Test
  void testIsTest() {
    assertTrue(SyncronizeContext.builder().test(true).build().isTest(), "isTest should be true");
  }

  @Test
  void testIsDeleteForce() {
    assertTrue(
        SyncronizeContext.builder().deleteForce(true).build().isDeleteForce(),
        "isDeleteForce should be true");
  }

  @Test
  void testIsNotifyLegalCollisions() {
    assertTrue(
        SyncronizeContext.builder().notifyLegalCollisions(true).build().isNotifyLegalCollisions(),
        "isNotifyLegalCollisions should be true");
  }

  @Test
  void testGetPublihser() {
    assertInstanceOf(
        Publisher.class, SyncronizeContext.builder().publisher(mock()).build().getPublisher());
  }

  @Test
  void testGetNotifier() {
    assertInstanceOf(
        SyncNotifier.class, SyncronizeContext.builder().notifier(mock()).build().getNotifier());
  }

  @Test
  void testDeleteFile() throws IOException {

    Path workDir = this.workDir.resolve("testDeleteFile");
    Files.createDirectories(workDir);

    Path path = workDir.resolve("test.txt");
    Files.createFile(path);

    SyncronizeContext.builder().build().delete(path);

    assertFalse(Files.exists(path), "File should be deleted");
  }

  @Test
  void testDeleteFileAccessDenied() throws IOException {

    Path workDir = this.workDir.resolve("testDeleteFileAccessDenied");
    Files.createDirectories(workDir);

    Path path = workDir.resolve("test.txt");
    Files.createFile(path);

    Files.setPosixFilePermissions(workDir, PosixFilePermissions.fromString("r--r--r--"));

    SyncronizeContext.builder().build().delete(path);

    assertFalse(Files.exists(path), "File should't be deleted");
  }

  @Test
  void testDeleteRecursive() throws IOException {

    Path workDir = this.workDir.resolve("testDeleteFile");

    Path subdir = workDir.resolve("subdir");
    Files.createDirectories(subdir);

    Path path = subdir.resolve("test.txt");
    Files.createFile(path);

    SyncronizeContext.builder().build().delete(workDir);

    assertFalse(Files.exists(workDir), "Directory should be deleted");
  }

  @Test
  void testDeleteRecursiveAccessDenied() throws IOException {

    Path workDir = this.workDir.resolve("testDeleteRecursiveAccessDenied");
    Files.createDirectories(workDir);

    Path subdir = workDir.resolve("subdir");
    Files.createDirectories(subdir);

    Path path = subdir.resolve("test.txt");
    Files.createFile(path);

    Files.setPosixFilePermissions(subdir, PosixFilePermissions.fromString("r--r--r--"));

    SyncronizeContext.builder().build().delete(workDir);

    assertTrue(Files.exists(workDir), "Directory should't deleted");
  }

  @Test
  void testIsDirectory() throws IOException {

    Path workDir = this.workDir.resolve("testIsDirectory");
    Files.createDirectories(workDir);

    assertTrue(
        SyncronizeContext.builder().build().isDirectory(workDir), "Directory check should be true");
  }

  @Test
  void testExists() throws IOException {

    Path workDir = this.workDir.resolve("testIsDirectory");
    Files.createDirectories(workDir);

    Path path = workDir.resolve("test.txt");
    Files.createFile(path);

    assertTrue(SyncronizeContext.builder().build().exists(path), "should be exists");
  }
}
