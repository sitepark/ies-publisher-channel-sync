package com.sitepark.ies.publisher.channel.sync.service;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jspecify.annotations.Nullable;

public class ChannelFactory {
  public Channel create(Path path) {
    for (Path channelRoot = path; channelRoot != null; channelRoot = channelRoot.getParent()) {
      ChannelLayout layout = identifyLayout(channelRoot);
      if (layout != null) {
        return new Channel(layout, channelRoot);
      }
    }

    throw new IllegalArgumentException("Cannot identify channel for path: " + path);
  }

  private @Nullable ChannelLayout identifyLayout(Path path) {
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
