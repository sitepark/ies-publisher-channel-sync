package com.sitepark.ies.publisher.channel.sync.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PublicationTypeTest {

  @Test
  void testGetPath() {
    assertEquals(Path.of("objects"), PublicationType.OBJECT.getPath(), "unexpected path");
  }
}
