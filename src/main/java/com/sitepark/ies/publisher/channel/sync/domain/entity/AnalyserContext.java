package com.sitepark.ies.publisher.channel.sync.domain.entity;

import com.sitepark.ies.publisher.channel.sync.service.Channel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnalyserContext {
  private final Channel channel;
  private final PublicationType publicationType;
  private final Path base;
  private final Path directory;
  private final PublicationDirectory iesDirectory;
  private final Map<String, PublishedPath> directoryEntries;
  private final boolean recursive;
  private final ResultEntryFactory resultEntryFactory;
  private final AnalyserResultFactory analyserResultFactory;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public AnalyserContext(
      Channel channel,
      PublicationType publicationType,
      Path base,
      Path directory,
      PublicationDirectory iesDirectory,
      Map<String, PublishedPath> directoryEntries,
      boolean recursive) {
    this.channel = channel;
    this.publicationType = publicationType;
    this.base = base;
    this.directory = directory;
    this.iesDirectory = iesDirectory;
    this.directoryEntries = Collections.unmodifiableMap(directoryEntries);
    this.recursive = recursive;
    this.resultEntryFactory = new ResultEntryFactory(this);
    this.analyserResultFactory = new AnalyserResultFactory(this.resultEntryFactory);
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Channel getChannel() {
    return this.channel;
  }

  public PublicationType getPublicationType() {
    return this.publicationType;
  }

  public Path getBase() {
    return this.base;
  }

  public Path getDirectory() {
    return this.directory;
  }

  public PublicationDirectory getIesDirectory() {
    return this.iesDirectory;
  }

  public Collection<PublishedPath> getDirectoryEntries() {
    return this.directoryEntries.values();
  }

  public boolean hasDirectoryEntry(String name) {
    return this.directoryEntries.containsKey(name);
  }

  public boolean isRecursive() {
    return this.recursive;
  }

  public ResultEntryFactory getResultEntryFactory() {
    return this.resultEntryFactory;
  }

  public AnalyserResultFactory getAnalyserResultFactory() {
    return this.analyserResultFactory;
  }

  public static Builder builder() {
    return new Builder();
  }

  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  public static class Builder {
    private Channel channel;
    private PublicationType publicationType;
    private Path base;
    private Path directory;
    private PublicationDirectory iesDirectory;

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private final Map<String, PublishedPath> directoryEntries = new HashMap<>();

    private boolean recursive;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public Builder channel(Channel channel) {
      this.channel = channel;
      return this;
    }

    public Builder publicationType(PublicationType publicationType) {
      this.publicationType = publicationType;
      return this;
    }

    public Builder base(Path base) {
      this.base = base;
      return this;
    }

    public Builder directory(Path directory) {
      this.directory = directory;
      return this;
    }

    public Builder iesDirectory(PublicationDirectory iesDirectory) {
      this.iesDirectory = iesDirectory;
      return this;
    }

    public Builder directoryEntry(PublishedPath directoryEntry) {

      if (this.directoryEntries.containsKey(directoryEntry.baseName())) {
        throw new IllegalArgumentException(
            "Duplicate entry: " + directoryEntry.baseName() + ", type:" + directoryEntry.type());
      }

      this.directoryEntries.put(directoryEntry.baseName(), directoryEntry);

      return this;
    }

    public Builder recursive(boolean recursive) {
      this.recursive = recursive;
      return this;
    }

    public AnalyserContext build() {
      return new AnalyserContext(
          this.channel,
          this.publicationType,
          this.base,
          this.directory,
          this.iesDirectory,
          this.directoryEntries,
          this.recursive);
    }
  }
}
