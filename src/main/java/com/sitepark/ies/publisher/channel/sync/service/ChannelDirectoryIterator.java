package com.sitepark.ies.publisher.channel.sync.service;

import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
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
    String fileName = this.fileName(path);
    return new PublishedPath(this.type, path.toAbsolutePath(), fileName);
  }

  private String fileName(Path path) {
    Path fileName = path.getFileName();
    if (fileName == null) {
      return null;
    }
    String fileNameString = fileName.toString();
    if (fileNameString.isBlank()) {
      return null;
    }
    return fileNameString;
  }
}
