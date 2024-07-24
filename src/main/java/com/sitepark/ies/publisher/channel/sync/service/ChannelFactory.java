package com.sitepark.ies.publisher.channel.sync.service;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChannelFactory {
  public Channel create(Path path) {
    Path channelRoot = path;
    do {
      ChannelLayout layout = identifyLayout(channelRoot);
      if (layout != null) {
        return new Channel(layout, channelRoot);
      }
    } while ((channelRoot = channelRoot.getParent()) != null);

    throw new IllegalArgumentException("Cannot identify channel for path: " + path);
  }

  private ChannelLayout identifyLayout(Path path) {
    if (this.isDocumentRootLayout(path)) {
      return ChannelLayout.DOCUMENT_ROOT;
    }
    if (this.isResourcesLayout(path)) {
      return ChannelLayout.RESOURCES;
    }
    return null;
  }

  private boolean isDocumentRootLayout(Path path) {
    return Files.exists(path.resolve("WEB-IES").resolve("context.php"));
  }

  private boolean isResourcesLayout(Path path) {
    return Files.exists(path.resolve("context.php")) && Files.isDirectory(path.resolve("objects"));
  }
}
