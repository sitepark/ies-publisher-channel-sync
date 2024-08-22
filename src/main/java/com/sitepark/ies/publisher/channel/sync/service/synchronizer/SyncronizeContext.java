package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import com.sitepark.ies.publisher.channel.sync.port.Publisher;
import com.sitepark.ies.publisher.channel.sync.port.SyncNotifier;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class SyncronizeContext {

  private final boolean test;

  private final boolean deleteForce;

  private final boolean notifyLegalCollisions;

  private final Publisher publisher;

  private final SyncNotifier notifier;

  private SyncronizeContext(
      boolean test,
      boolean deleteForce,
      boolean notifyLegalCollisions,
      Publisher publisher,
      SyncNotifier notifier) {
    this.test = test;
    this.deleteForce = deleteForce;
    this.notifyLegalCollisions = notifyLegalCollisions;
    this.publisher = publisher;
    this.notifier = notifier;
  }

  public boolean isTest() {
    return this.test;
  }

  public boolean isDeleteForce() {
    return this.deleteForce;
  }

  public boolean isNotifyLegalCollisions() {
    return this.notifyLegalCollisions;
  }

  public Publisher getPublisher() {
    return this.publisher;
  }

  public SyncNotifier getNotifier() {
    return this.notifier;
  }

  public boolean delete(Path path) {
    try {
      if (Files.isDirectory(path)) {
        Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
      } else {
        Files.delete(path);
      }
      return true;
    } catch (IOException | UncheckedIOException e) {
      return false;
    }
  }

  public boolean isDirectory(Path path) {
    return Files.isDirectory(path);
  }

  public boolean exists(Path path) {
    return Files.exists(path);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private boolean test;
    private boolean deleteForce;
    private boolean notifyLegalCollisions;
    private Publisher publisher;
    private SyncNotifier notifier;

    public Builder test(boolean test) {
      this.test = test;
      return this;
    }

    public Builder deleteForce(boolean deleteForce) {
      this.deleteForce = deleteForce;
      return this;
    }

    public Builder notifyLegalCollisions(boolean notifyLegalCollisions) {
      this.notifyLegalCollisions = notifyLegalCollisions;
      return this;
    }

    public Builder publisher(Publisher publisher) {
      this.publisher = publisher;
      return this;
    }

    public Builder notifier(SyncNotifier notifier) {
      this.notifier = notifier;
      return this;
    }

    public SyncronizeContext build() {
      return new SyncronizeContext(
          this.test, this.deleteForce, this.notifyLegalCollisions, this.publisher, this.notifier);
    }
  }
}
