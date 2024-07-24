package com.sitepark.ies.publisher.channel.sync.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import java.nio.file.Path;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class ChannelTest {

  private final Path root = Path.of("root").toAbsolutePath();

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    EqualsVerifier.forClass(Channel.class).verify();
  }

  @Test
  void testResolveWithPath() {
    Channel channel = new Channel(ChannelLayout.RESOURCES, this.root);
    assertEquals(
        this.root.resolve("objects/abc"),
        channel.resolve(PublicationType.OBJECT, Path.of("abc")),
        "The resolved path should be equal");
  }

  @Test
  void testResolveWithString() {
    Channel channel = new Channel(ChannelLayout.RESOURCES, this.root);
    assertEquals(
        this.root.resolve("objects/abc"),
        channel.resolve(PublicationType.OBJECT, "abc"),
        "The resolved path should be equal");
  }

  @Test
  void testRelativize() {
    Channel channel = new Channel(ChannelLayout.RESOURCES, this.root);
    assertEquals(
        Path.of("abc/cde/efg"),
        channel.relativize(PublicationType.OBJECT, this.root.resolve("objects/abc/cde/efg")),
        "The relativized path should be equal");
  }
}
