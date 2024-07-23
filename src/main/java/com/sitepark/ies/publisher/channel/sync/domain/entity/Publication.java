package com.sitepark.ies.publisher.channel.sync.domain.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.file.Path;
import java.util.Objects;

public record Publication(
    PublicationType type,
    String fileName,
    Ref object,
    Path path,
    boolean isPublished,
    Ref collidesWith,
    Path absolutePath,
    String hash) {

  public boolean isCollision() {
    return this.collidesWith != null;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder()
        .type(this.type)
        .object(this.object)
        .path(this.path)
        .isPublished(this.isPublished)
        .collidesWith(this.collidesWith)
        .absolutePath(this.absolutePath)
        .hash(this.hash);
  }

  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  public static final class Builder {
    private PublicationType type;
    private Ref object;
    private Path path;
    private boolean isPublished;
    private Ref collidesWith;
    private Path absolutePath;
    private String hash;

    public Builder type(PublicationType type) {
      this.type = type;
      return this;
    }

    public Builder object(Ref object) {
      this.object = object;
      return this;
    }

    public Builder path(Path path) {

      // remove leading slash
      Path root = path.getRoot();
      if (root != null) {
        this.path = root.relativize(path);
        return this;
      }

      this.path = path;
      return this;
    }

    public Builder isPublished() {
      return this.isPublished(true);
    }

    public Builder isPublished(boolean isPublished) {
      this.isPublished = isPublished;
      return this;
    }

    public Builder collidesWith(Ref collidesWith) {
      this.collidesWith = collidesWith;
      return this;
    }

    public Builder absolutePath(Path absolutePath) {
      this.absolutePath = absolutePath;
      return this;
    }

    public Builder hash(String hash) {
      this.hash = hash;
      return this;
    }

    @SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
    public Publication build() {

      Objects.requireNonNull(this.type, "type must not be null");
      Objects.requireNonNull(this.path, "path must not be null");

      Path fileName = this.path.getFileName();
      if (fileName == null) {
        throw new IllegalArgumentException("path must have a file name");
      }

      return new Publication(
          this.type,
          fileName.toString(),
          this.object,
          this.path,
          this.isPublished,
          this.collidesWith,
          this.absolutePath,
          this.hash);
    }
  }
}
