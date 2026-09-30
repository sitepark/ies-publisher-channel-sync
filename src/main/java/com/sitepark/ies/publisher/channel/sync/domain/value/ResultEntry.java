package com.sitepark.ies.publisher.channel.sync.domain.value;

import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import java.nio.file.Path;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class ResultEntry {

  final ResultType resultType;

  final PublicationDirectory publicationDirectory;

  @Nullable final Publication publication;

  @Nullable final PublishedPath publishedPath;

  final boolean deleteForce;

  final boolean temporary;

  @Nullable final Path absolutePath;

  private ResultEntry(
      ResultType resultType,
      PublicationDirectory publicationDirectory,
      @Nullable Publication publication,
      @Nullable PublishedPath publishedPath,
      boolean deleteForce,
      boolean temporary,
      @Nullable Path absolutePath) {
    this.resultType = resultType;
    this.publicationDirectory = publicationDirectory;
    this.publication = publication;
    this.publishedPath = publishedPath;
    this.deleteForce = deleteForce;
    this.temporary = temporary;
    this.absolutePath = absolutePath;
  }

  public static Builder builder() {
    return new Builder();
  }

  public ResultType getResultType() {
    return this.resultType;
  }

  public PublicationDirectory getPublicationDirectory() {
    return this.publicationDirectory;
  }

  public @Nullable Publication getPublication() {
    return this.publication;
  }

  public @Nullable PublishedPath getPublishedPath() {
    return this.publishedPath;
  }

  public boolean isDeleteForce() {
    return this.deleteForce;
  }

  public @Nullable Path getAbsolutePath() {
    return this.absolutePath;
  }

  public @Nullable Ref getObject() {
    if (this.publication == null) {
      return null;
    } else {
      return this.publication.object();
    }
  }

  public @Nullable String getName() {
    if (this.publication != null) {
      return this.publication.fileName();
    } else if (this.publishedPath != null) {
      return this.publishedPath.baseName();
    }
    return this.publicationDirectory.getName();
  }

  public boolean isTemporary() {
    return this.temporary;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        this.absolutePath,
        this.deleteForce,
        this.publicationDirectory,
        this.publication,
        this.publishedPath,
        this.resultType,
        this.temporary);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ResultEntry that)
        && Objects.equals(this.absolutePath, that.absolutePath)
        && this.deleteForce == that.deleteForce
        && Objects.equals(this.publicationDirectory, that.publicationDirectory)
        && Objects.equals(this.publication, that.publication)
        && Objects.equals(this.publishedPath, that.publishedPath)
        && this.resultType == that.resultType
        && this.temporary == that.temporary;
  }

  @Override
  public String toString() {
    return "ResultEntry [resultType="
        + resultType
        + ", publicationDirectory="
        + publicationDirectory
        + ", publication="
        + publication
        + ", publishedPath="
        + publishedPath
        + ", deleteForce="
        + deleteForce
        + ", temporary="
        + temporary
        + ", absolutePath="
        + absolutePath
        + "]";
  }

  // callers are expected to set the required fields, as before
  @SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "NullAway.Init"})
  public static final class Builder {

    private PublicationDirectory publicationDirectory;
    @Nullable private Publication publication;
    @Nullable private PublishedPath publishedPath;
    private ResultType resultType;
    private boolean deleteForce;
    private boolean temporary;

    public Builder publicationDirectory(PublicationDirectory publicationDirectory) {
      this.publicationDirectory = publicationDirectory;
      return this;
    }

    public Builder publication(@Nullable Publication publication) {
      this.publication = publication;
      return this;
    }

    public Builder publishedPath(@Nullable PublishedPath publishedPath) {
      this.publishedPath = publishedPath;
      return this;
    }

    public Builder resultType(ResultType resultType) {
      this.resultType = resultType;
      return this;
    }

    public Builder deleteForce(boolean deleteForce) {
      this.deleteForce = deleteForce;
      return this;
    }

    public Builder temporary(boolean temporary) {
      this.temporary = temporary;
      return this;
    }

    private @Nullable Path getAbsolutePath() {

      if (this.publication != null) {
        return this.publication.absolutePath();
      }
      if (this.publishedPath != null) {
        return this.publishedPath.absolutePath();
      }

      return null;
    }

    public ResultEntry build() {

      @Nullable Path absolutePath = this.getAbsolutePath();

      return new ResultEntry(
          this.resultType,
          this.publicationDirectory,
          this.publication,
          this.publishedPath,
          this.deleteForce,
          this.temporary,
          absolutePath);
    }
  }
}
