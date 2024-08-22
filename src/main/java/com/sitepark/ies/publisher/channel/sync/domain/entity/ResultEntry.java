package com.sitepark.ies.publisher.channel.sync.domain.entity;

import java.nio.file.Path;
import java.util.Objects;

public final class ResultEntry {

  final ResultType resultType;

  final PublicationDirectory publicationDirectory;

  final Publication publication;

  final PublishedPath publishedPath;

  final boolean deleteForce;

  final boolean temporary;

  final Path absolutePath;

  private ResultEntry(
      ResultType resultType,
      PublicationDirectory publicationDirectory,
      Publication publication,
      PublishedPath publishedPath,
      boolean deleteForce,
      boolean temporary,
      Path absolutePath) {
    this.resultType = resultType;
    this.publicationDirectory = publicationDirectory;
    this.publication = publication;
    this.publishedPath = publishedPath;
    this.deleteForce = deleteForce;
    this.temporary = temporary;
    this.absolutePath = absolutePath;
  }

  public ResultType getResultType() {
    return this.resultType;
  }

  public PublicationDirectory getPublicationDirectory() {
    return this.publicationDirectory;
  }

  public Publication getPublication() {
    return this.publication;
  }

  public PublishedPath getPublishedPath() {
    return this.publishedPath;
  }

  public boolean isDeleteForce() {
    return this.deleteForce;
  }

  public Path getAbsolutePath() {
    return this.absolutePath;
  }

  public Ref getObject() {
    if (this.publication == null) {
      return null;
    } else {
      return this.publication.object();
    }
  }

  public String getName() {
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

  public static Builder builder() {
    return new Builder();
  }

  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  public static final class Builder {

    private PublicationDirectory publicationDirectory;
    private Publication publication;
    private PublishedPath publishedPath;
    private ResultType resultType;
    private boolean deleteForce;
    private boolean temporary;

    public Builder publicationDirectory(PublicationDirectory publicationDirectory) {
      this.publicationDirectory = publicationDirectory;
      return this;
    }

    public Builder publication(Publication publication) {
      this.publication = publication;
      return this;
    }

    public Builder publishedPath(PublishedPath publishedPath) {
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

    private Path getAbsolutePath() {

      if (this.publication != null) {
        return this.publication.absolutePath();
      }
      if (this.publishedPath != null) {
        return this.publishedPath.absolutePath();
      }

      return null;
    }

    public ResultEntry build() {

      Path absolutePath = this.getAbsolutePath();

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
