package com.sitepark.ies.publisher.channel.sync.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class ChannelFactoryTest {

  private final Path resourceBase =
      Path.of("src/test/resources/service/ChannelFactoryTest").toAbsolutePath();

  @Test
  void testCreateChannelWithDocumentRootLayout() {

    ChannelFactory factory = new ChannelFactory();
    Channel channel = factory.create(this.resourceBase.resolve("documentRootLayout"));

    Channel expected =
        new Channel(ChannelLayout.DOCUMENT_ROOT, this.resourceBase.resolve("documentRootLayout"));

    assertEquals(expected, channel, "unsexpected channel");
  }

  @Test
  void testCreateChannelWithDocumentRootLayoutAndInnerPath() {

    ChannelFactory factory = new ChannelFactory();
    Channel channel =
        factory.create(this.resourceBase.resolve("documentRootLayout").resolve("a/b"));

    Channel expected =
        new Channel(ChannelLayout.DOCUMENT_ROOT, this.resourceBase.resolve("documentRootLayout"));

    assertEquals(expected, channel, "unsexpected channel");
  }

  @Test
  void testCreateChannelWithResourcesLayout() {

    ChannelFactory factory = new ChannelFactory();
    Channel channel = factory.create(this.resourceBase.resolve("resourcesLayout"));
    Channel expected =
        new Channel(ChannelLayout.RESOURCES, this.resourceBase.resolve("resourcesLayout"));

    assertEquals(expected, channel, "unsexpected channel");
  }

  @Test
  void testCreateChannelWithResourcesLayoutWithInnerPath() {

    ChannelFactory factory = new ChannelFactory();
    Channel channel =
        factory.create(this.resourceBase.resolve("resourcesLayout").resolve("objects/a/b"));
    Channel expected =
        new Channel(ChannelLayout.RESOURCES, this.resourceBase.resolve("resourcesLayout"));

    assertEquals(expected, channel, "unsexpected channel");
  }
}
