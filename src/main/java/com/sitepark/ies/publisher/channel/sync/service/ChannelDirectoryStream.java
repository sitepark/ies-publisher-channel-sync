package com.sitepark.ies.publisher.channel.sync.service;

import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jspecify.annotations.Nullable;

public class ChannelDirectoryStream implements DirectoryStream<PublishedPath> {

  private final PublicationType type;

  private final java.nio.file.DirectoryStream<Path> stream;

  private final Lock lock = new ReentrantLock();

  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  @Nullable
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
    try (CloseableLock ignored = new CloseableLock(lock)) {
      if (this.iterator != null) {
        throw new IllegalStateException("Iterator already obtained");
      }
      this.iterator = new ChannelDirectoryIterator(this.type, stream.iterator());
      return this.iterator;
    }
  }

  static final class CloseableLock implements AutoCloseable {
    private final Lock lock;

    CloseableLock(Lock lock) {
      this.lock = lock;
      lock.lock();
    }

    @Override
    public void close() {
      lock.unlock();
    }
  }
}
