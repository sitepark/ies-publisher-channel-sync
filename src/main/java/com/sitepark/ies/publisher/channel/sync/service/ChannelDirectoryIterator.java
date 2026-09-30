package com.sitepark.ies.publisher.channel.sync.service;

import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import java.nio.file.Path;
import java.util.Iterator;

public class ChannelDirectoryIterator implements Iterator<PublishedPath> {

  private final PublicationType type;

  private final Iterator<Path> iterator;

  protected ChannelDirectoryIterator(PublicationType type, Iterator<Path> iterator) {
    this.type = type;
    this.iterator = iterator;
  }

  @Override
  public boolean hasNext() {
    return this.iterator.hasNext();
  }

  @Override
  public PublishedPath next() {
    Path path = this.iterator.next();
    return new PublishedPath(this.type, path.toAbsolutePath(), fileName(path));
  }

  // The entries of a directory stream always have a file name, only a root path has none.
  private static String fileName(Path path) {
    Path fileName = path.getFileName();
    if (fileName == null) {
      throw new IllegalStateException("Directory entry without file name: " + path);
    }
    return fileName.toString();
  }
}
