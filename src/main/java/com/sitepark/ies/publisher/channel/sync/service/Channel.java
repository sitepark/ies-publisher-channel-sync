package com.sitepark.ies.publisher.channel.sync.service;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

// record-style accessors of an immutable value
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidFieldNameMatchingMethodName"})
public final class Channel {

  private final ChannelLayout layout;
  private final Path root;
  private final @Nullable IdPathMapper idPathMapper;

  private Channel(ChannelLayout layout, Path root, @Nullable IdPathMapper idPathMapper) {
    this.layout = layout;
    this.root = root;
    this.idPathMapper = idPathMapper;
  }

  public static Channel of(ChannelLayout layout, Path root) {
    if (!root.isAbsolute()) {
      throw new IllegalArgumentException("Root path must be absolute");
    }

    @Nullable IdPathMapper idPathMapper;
    if (layout == ChannelLayout.ID_BASED_RESOURCES) {
      idPathMapper = new IdPathMapper(new FixedDecimalGroupingStrategy(2, 3));
    } else {
      idPathMapper = null;
    }

    return new Channel(layout, root, idPathMapper);
  }

  public ChannelLayout layout() {
    return this.layout;
  }

  public Path root() {
    return this.root;
  }

  public Path pathFor(long id) {
    if (this.idPathMapper == null) {
      throw new IllegalStateException("Layout " + this.layout + " has no id based paths");
    }
    return this.idPathMapper.pathFor(id);
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

  @Override
  public int hashCode() {
    return Objects.hash(this.layout, this.root, this.idPathMapper);
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (!(o instanceof Channel that)) {
      return false;
    }
    return Objects.equals(this.layout, that.layout)
        && Objects.equals(this.root, that.root)
        && Objects.equals(this.idPathMapper, that.idPathMapper);
  }

  @Override
  public String toString() {
    return "Channel{"
        + "layout="
        + layout
        + ", root="
        + root
        + ", idPathMapper="
        + idPathMapper
        + '}';
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

  // the returned stream owns the wrapped stream and closes it
  @SuppressWarnings("StreamResourceLeak")
  public DirectoryStream<PublishedPath> newDirectoryStream(PublicationType type, Path path)
      throws IOException {
    Path absolutePath = this.resolve(type, path);
    return new ChannelDirectoryStream(type, Files.newDirectoryStream(absolutePath));
  }
}
