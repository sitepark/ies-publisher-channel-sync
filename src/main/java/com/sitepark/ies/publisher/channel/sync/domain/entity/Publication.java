package com.sitepark.ies.publisher.channel.sync.domain.entity;

import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.value.Ref;
import java.nio.file.Path;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public record Publication(
    long id,
    long mediaId,
    PublicationType type,
    String fileName,
    Ref object,
    Path path,
    boolean isPublished,
    @Nullable Ref collidesWith,
    Path absolutePath,
    @Nullable Path absolutePathMediaOwner,
    @Nullable String hash) {

  public static Builder builder() {
    return new Builder();
  }

  public boolean isCollision() {
    return this.collidesWith != null;
  }

  // required fields are validated in build()
  @SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "NullAway.Init"})
  public static final class Builder {
    private long id;
    private long mediaId;
    private PublicationType type;
    private Ref object;
    private Path path;
    private String fileName;
    private boolean isPublished;
    @Nullable private Ref collidesWith;
    private Path absolutePath;
    @Nullable private Path absolutePathMediaOwner;
    @Nullable private String hash;

    public Builder id(long id) {
      this.id = id;
      return this;
    }

    public Builder mediaId(long mediaId) {
      this.mediaId = mediaId;
      return this;
    }

    public Builder type(PublicationType type) {
      this.type = type;
      return this;
    }

    public Builder object(Ref object) {
      this.object = object;
      return this;
    }

    public Builder path(Path path) {

      Objects.requireNonNull(path, "path must not be null");

      Path fileName = path.getFileName();

      if (fileName == null) {
        throw new IllegalArgumentException("path must have a file name");
      }
      this.fileName = fileName.toString();

      Path relativizePath = path;
      Path root = path.getRoot();
      if (root != null) {
        relativizePath = root.relativize(path);
      }

      this.path = relativizePath;
      return this;
    }

    public Builder isPublished() {
      return this.isPublished(true);
    }

    public Builder isPublished(boolean isPublished) {
      this.isPublished = isPublished;
      return this;
    }

    public Builder collidesWith(@Nullable Ref collidesWith) {
      this.collidesWith = collidesWith;
      return this;
    }

    public Builder absolutePath(Path absolutePath) {
      this.absolutePath = absolutePath;
      return this;
    }

    public Builder absolutePathMediaOwner(@Nullable Path absolutePathMediaOwner) {
      this.absolutePathMediaOwner = absolutePathMediaOwner;
      return this;
    }

    public Builder hash(@Nullable String hash) {
      this.hash = hash;
      return this;
    }

    public Publication build() {

      Objects.requireNonNull(this.type, "type must not be null");
      Objects.requireNonNull(this.path, "path must not be null");

      return new Publication(
          this.id,
          this.mediaId,
          this.type,
          this.fileName,
          this.object,
          this.path,
          this.isPublished,
          this.collidesWith,
          this.absolutePath,
          this.absolutePathMediaOwner,
          this.hash);
    }
  }
}
