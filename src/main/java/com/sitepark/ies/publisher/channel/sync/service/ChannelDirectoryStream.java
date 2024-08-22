package com.sitepark.ies.publisher.channel.sync.service;

import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;

public class ChannelDirectoryStream implements DirectoryStream<PublishedPath> {

  private final PublicationType type;

  private final java.nio.file.DirectoryStream<Path> stream;

  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  private ChannelDirectoryIterator iterator;

  protected ChannelDirectoryStream(
      PublicationType type, java.nio.file.DirectoryStream<Path> stream) {
    this.type = type;
    this.stream = stream;
  }

  @Override
  public void close() throws IOException {
    this.stream.close();
  }

  @Override
  public Iterator<PublishedPath> iterator() {
    synchronized (this) {
      if (this.iterator != null) {
        throw new IllegalStateException("Iterator already obtained");
      }
      this.iterator = new ChannelDirectoryIterator(this.type, stream.iterator());
      return this.iterator;
    }
  }
}
