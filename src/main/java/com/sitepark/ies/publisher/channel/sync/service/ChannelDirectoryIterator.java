package com.sitepark.ies.publisher.channel.sync.service;

import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import java.nio.file.Path;
import java.util.Iterator;
import org.jspecify.annotations.Nullable;

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

  // A directory entry always has a non-blank file name, null is only possible for a path without
  // name elements.
  @SuppressWarnings("NullAway")
  @Override
  public PublishedPath next() {
    Path path = this.iterator.next();
    String fileName = this.fileName(path);
    return new PublishedPath(this.type, path.toAbsolutePath(), fileName);
  }

  private @Nullable String fileName(Path path) {
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
