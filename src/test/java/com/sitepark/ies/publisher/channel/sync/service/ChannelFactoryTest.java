package com.sitepark.ies.publisher.channel.sync.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class ChannelFactoryTest {

  private final Path resourceBase =
      Path.of("src/test/resources/service/ChannelFactoryTest").toAbsolutePath();

  private final Path documentRootLayoutBase = this.resourceBase.resolve("documentRootLayout");

  private final Path resourcesLayoutBase = this.resourceBase.resolve("resourcesLayout");

  @Test
  void testCreateChannelWithDocumentRootLayout() {

    ChannelFactory factory = new ChannelFactory();
    Channel channel = factory.create(this.documentRootLayoutBase);

    Channel expected =
        new Channel(ChannelLayout.DOCUMENT_ROOT, this.resourceBase.resolve("documentRootLayout"));

    assertEquals(expected, channel, "unsexpected channel");
  }

  @Test
  void testCreateChannelWithDocumentRootLayoutAndInnerPath() {

    ChannelFactory factory = new ChannelFactory();
    Channel channel = factory.create(this.documentRootLayoutBase.resolve("a/b"));

    Channel expected =
        new Channel(ChannelLayout.DOCUMENT_ROOT, this.resourceBase.resolve("documentRootLayout"));

    assertEquals(expected, channel, "unsexpected channel");
  }

  @Test
  void testCreateChannelWithResourcesLayout() {

    ChannelFactory factory = new ChannelFactory();
    Channel channel = factory.create(this.resourcesLayoutBase.resolve("valid"));
    Channel expected =
        new Channel(ChannelLayout.RESOURCES, this.resourcesLayoutBase.resolve("valid"));

    assertEquals(expected, channel, "unsexpected channel");
  }

  @Test
  void testCreateChannelWithResourcesLayoutWithMissingObjectsDir() {

    ChannelFactory factory = new ChannelFactory();
    assertThrows(
        IllegalArgumentException.class,
        () -> factory.create(this.resourcesLayoutBase.resolve("missingObjectsDir")),
        "Cannot identify channel for path: "
            + this.resourcesLayoutBase.resolve("missingObjectsDir"));
  }

  @Test
  void testCreateChannelWithResourcesLayoutWithInnerPath() {

    ChannelFactory factory = new ChannelFactory();
    Channel channel =
        factory.create(this.resourcesLayoutBase.resolve("valid").resolve("objects/a/b"));
    Channel expected =
        new Channel(ChannelLayout.RESOURCES, this.resourcesLayoutBase.resolve("valid"));

    assertEquals(expected, channel, "unsexpected channel");
  }

  @Test
  void testIndentifyFailed() {

    ChannelFactory factory = new ChannelFactory();
    assertThrows(
        IllegalArgumentException.class,
        () -> factory.create(this.resourceBase.resolve("unknownLayout")),
        "Cannot identify channel for path: " + this.resourceBase.resolve("unknownLayout"));
  }
}
