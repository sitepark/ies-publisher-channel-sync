package com.sitepark.ies.publisher.channel.sync.service;

import static org.junit.jupiter.api.Assertions.*;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.TooManyMethods")
class ChannelTest {

  private final Path root = Path.of("src/test/resources/service/ChannelTest").toAbsolutePath();

  @Test
  void testNonAbsoluteRoot() {
    Path root = Path.of("root");
    assertThrows(
        IllegalArgumentException.class,
        () -> new Channel(ChannelLayout.RESOURCES, root),
        "Root path must be absolute");
  }

  @Test
  void testGetRoot() {
    Channel channel = new Channel(ChannelLayout.RESOURCES, this.root);
    assertEquals(this.root, channel.root(), "The root path should be equal");
  }

  @Test
  void testGetLayout() {
    Channel channel = new Channel(ChannelLayout.RESOURCES, this.root);
    assertEquals(ChannelLayout.RESOURCES, channel.layout(), "The layout should be equal");
  }

  @Test
  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  void testResolveWithPathFromDocumentRootLayout() {
    Channel channel = new Channel(ChannelLayout.DOCUMENT_ROOT, this.root);
    assertEquals(
        this.root.resolve("abc"),
        channel.resolve(PublicationType.OBJECT, Path.of("abc")),
        "The resolved path should be equal");
  }

  @Test
  void testResolveWithPathFromResourcesLayout() {
    Channel channel = new Channel(ChannelLayout.RESOURCES, this.root);
    assertEquals(
        this.root.resolve("objects/abc"),
        channel.resolve(PublicationType.OBJECT, Path.of("abc")),
        "The resolved path should be equal");
  }

  @Test
  void testResolveWithStringFromResourcesLayout() {
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

  @Test
  void testExists() {
    Channel channel = new Channel(ChannelLayout.RESOURCES, this.root);
    Path path = this.root.resolve("testExists");
    assertTrue(channel.exists(PublicationType.OBJECT, path), "The path should exist");
  }

  @Test
  void testToPublicationPathWithDocumentRootLayout() {
    Channel channel = new Channel(ChannelLayout.DOCUMENT_ROOT, this.root);
    Path path = this.root.resolve("abc");
    assertEquals(
        Path.of("abc"), channel.toPublicationPath(path), "The publication path should be equal");
  }

  @Test
  void testToPublicationPathWithResourceLayout() {
    Channel channel = new Channel(ChannelLayout.RESOURCES, this.root);
    Path path = this.root.resolve("objects/abc");
    assertEquals(
        Path.of("abc"), channel.toPublicationPath(path), "The publication path should be equal");
  }

  @Test
  void testToPublicationPathWithUnknownType() {
    Channel channel = new Channel(ChannelLayout.RESOURCES, this.root);
    Path path = this.root.resolve("unknown/abc");
    assertThrows(
        IllegalArgumentException.class,
        () -> channel.toPublicationPath(path),
        "Unknown publication type for path: unknown/abc");
  }

  @Test
  void testNewDirectoryStream() throws IOException {
    Channel channel = new Channel(ChannelLayout.RESOURCES, this.root);
    assertInstanceOf(
        ChannelDirectoryStream.class,
        channel.newDirectoryStream(PublicationType.OBJECT, this.root));
  }
}
