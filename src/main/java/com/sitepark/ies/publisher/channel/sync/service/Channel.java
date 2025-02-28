package com.sitepark.ies.publisher.channel.sync.service;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("PMD.TooManyMethods")
public record Channel(ChannelLayout layout, Path root) {

  public Channel {
    if (!root.isAbsolute()) {
      throw new IllegalArgumentException("Root path must be absolute");
    }
  }

  public Path resolve(PublicationType type, String path) {
    return this.toPublisherTypeBase(type).resolve(path);
  }

  public Path resolve(PublicationType type, Path path) {
    return this.toPublisherTypeBase(type).resolve(path);
  }

  public boolean exists(PublicationType type, Path path) {
    return Files.exists(this.resolve(type, path));
  }

  private Path toPublisherTypeBase(PublicationType type) {

    if (this.layout == ChannelLayout.DOCUMENT_ROOT) {
      return this.root;
    }

    return this.root.resolve(type.getPath());
  }

  public Path toPublicationPath(Path path) {

    if (this.layout() == ChannelLayout.DOCUMENT_ROOT) {
      return this.relativize(PublicationType.OBJECT, path);
    }

    Path relativePath = this.root.relativize(path);

    PublicationType type = this.getTypeByPath(relativePath);
    return this.relativize(type, path);
  }

  private PublicationType getTypeByPath(Path path) {

    for (PublicationType type : PublicationType.values()) {
      if (path.startsWith(type.getPath())) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown publication type for path: " + path);
  }

  public Path relativize(PublicationType type, Path path) {
    return this.toPublisherTypeBase(type).relativize(path);
  }

  public DirectoryStream<PublishedPath> newDirectoryStream(PublicationType type, Path path)
      throws IOException {
    Path absolutePath = this.resolve(type, path);
    return new ChannelDirectoryStream(type, Files.newDirectoryStream(absolutePath));
  }
}
