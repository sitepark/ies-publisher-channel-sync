package com.sitepark.ies.publisher.channel.sync.service;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class Channel {

  private final Path root;

  private final ChannelLayout layout;

  public Channel(ChannelLayout layout, Path root) {
    if (!root.isAbsolute()) {
      throw new IllegalArgumentException("Root path must be absolute");
    }
    this.layout = layout;
    this.root = root;
  }

  public Path getRoot() {
    return this.root;
  }

  public ChannelLayout getLayout() {
    return this.layout;
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

    if (this.getLayout() == ChannelLayout.DOCUMENT_ROOT) {
      return this.relativize(PublicationType.OBJECT, path);
    }

    Path relativePath = this.root.relativize(path);

    if (relativePath.getNameCount() == 0) {
      return Path.of("");
    }

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

  @Override
  public int hashCode() {
    return Objects.hash(this.layout, this.root);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Channel that)
        && Objects.equals(this.layout, that.layout)
        && Objects.equals(this.root, that.root);
  }
}
