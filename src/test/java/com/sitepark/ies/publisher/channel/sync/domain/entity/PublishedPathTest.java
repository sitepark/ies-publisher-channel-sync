package com.sitepark.ies.publisher.channel.sync.domain.entity;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PublishedPathTest {

  @Test
  void testIsDirectory() {
    PublishedPath path =
        new PublishedPath(
            PublicationType.OBJECT, Path.of("src/test/java").toAbsolutePath(), "java");
    assertTrue(path.isDirectory(), "path should be a directory");
  }
}
