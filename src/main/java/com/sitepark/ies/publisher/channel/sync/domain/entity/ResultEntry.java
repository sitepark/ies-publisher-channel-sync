package com.sitepark.ies.publisher.channel.sync.domain.entity;

import java.nio.file.Path;

public record ResultEntry(
    ResultType resultType,
    PublicationDirectory iesDirectory,
    Publication publication,
    PublishedPath publishedPath,
    boolean deleteForce,
    boolean temporary,
    Path absolutePath) {

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
    return this.iesDirectory.getName();
  }

  public boolean isTemporary() {
    return this.temporary;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder()
        .iesDirectory(this.iesDirectory)
        .publication(this.publication)
        .publishedPath(this.publishedPath)
        .resultType(this.resultType)
        .deleteForce(this.deleteForce);
  }

  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  public static final class Builder {

    private PublicationDirectory iesDirectory;
    private Publication publication;
    private PublishedPath publishedPath;
    private ResultType resultType;
    private boolean deleteForce;
    private boolean temporary;

    public Builder iesDirectory(PublicationDirectory iesDirectory) {
      this.iesDirectory = iesDirectory;
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
          this.iesDirectory,
          this.publication,
          this.publishedPath,
          this.deleteForce,
          this.temporary,
          absolutePath);
    }
  }
}
