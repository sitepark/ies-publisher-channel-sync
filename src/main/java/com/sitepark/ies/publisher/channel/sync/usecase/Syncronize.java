package com.sitepark.ies.publisher.channel.sync.usecase;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import com.sitepark.ies.publisher.channel.sync.port.Publisher;
import com.sitepark.ies.publisher.channel.sync.port.SyncNotifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class Syncronize {

  private final boolean test;

  private final boolean deleteForce;

  private final boolean notifyLegalCollisions;

  private final Publisher publisher;

  private final SyncNotifier notifier;

  public Syncronize(
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

  public void syncronize(AnalyserResult result) throws IOException {

    List<ResultEntry> results = result.entries();

    for (ResultEntry entry : results) {

      if (entry.resultType() == ResultType.UNKNOWN_FILE_OR_DIRECTORY) {
        this.processUnknownFileOrDirectory(entry);
      } else if (entry.resultType() == ResultType.FILE_DIRECTORY_MISMATCH) {
        this.processFileDirectoryMismatch(entry);
      } else if (entry.resultType() == ResultType.HASH_MISMATCH
          || entry.resultType() == ResultType.MISSING_FILE
          || entry.resultType() == ResultType.LOST_PUBLICATION) {

        this.processMissingOrInvalidFile(entry);

      } else if (entry.resultType() == ResultType.ILLEGAL_FILENAME_COLLISION) {
        this.processIllegalFileCollisions(entry);
      } else if (entry.resultType() == ResultType.LEGAL_FILENAME_COLLISION) {
        if (this.notifyLegalCollisions) {
          this.notifier.notify(entry, "legal collision " + entry.absolutePath() + " (skip)");
        }
      } else if (entry.resultType() == ResultType.TEMPLATE_MISSING) {
        this.notifier.notify(
            entry, "published " + entry.absolutePath() + " (ignored, template missing)");
      } else {
        this.notifier.notify(
            entry,
            "unkown result '" + entry.resultType().getSymbol() + "'   " + entry.absolutePath());
      }
    }
  }

  private boolean delete(Path path) throws IOException {
    if (Files.isDirectory(path)) {
      Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    } else {
      Files.delete(path);
    }
    return Files.exists(path);
  }

  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  private void processUnknownFileOrDirectory(ResultEntry entry) throws IOException {

    if (entry.isTemporary()) {
      this.notifier.notify(entry, "deleted " + entry.absolutePath() + " (ignored, is temporary)");
      return;
    }

    Path entryFile = entry.absolutePath();

    boolean deleteDirectoryForce = entry.deleteForce() || this.deleteForce;
    if (Files.isDirectory(entryFile) && !deleteDirectoryForce) {
      this.notifier.notify(
          entry,
          "deleted   " + entry.absolutePath() + " (ignore, is directory, use --force-delete)");
      return;
    }

    boolean success = true;

    if (!this.test) {
      success = this.delete(entryFile);
    }

    if (success) {
      this.notifier.notify(
          entry, "deleted   " + entry.absolutePath() + (this.test ? " (test)" : ""));
    } else {
      this.notifier.notify(entry, "deleted   " + entry.absolutePath() + " (failed)");
    }
  }

  /** Delete directory and republish */
  private void processFileDirectoryMismatch(ResultEntry entry) throws IOException {

    boolean success = true;
    if (!this.test) {
      success = this.delete(entry.absolutePath());
    }

    if (!success) {
      this.notifier.notify(entry, "deleted   " + entry.absolutePath() + " (failed)");
      return;
    }

    this.notifier.notify(entry, "deleted   " + entry.absolutePath() + (this.test ? " (test)" : ""));
    try {
      if (!this.test) {
        this.publisher.republish(entry.getObject());
      }
      this.notifier.notify(
          entry, "republish   " + entry.absolutePath() + (this.test ? " (test)" : ""));
    } catch (Throwable t) {
      this.notifier.notify(
          entry, "republish   " + entry.absolutePath() + " (failed: " + t.getMessage() + ")", t);
    }
  }

  private void processMissingOrInvalidFile(ResultEntry entry) throws IOException {

    if (entry.isTemporary()) {
      this.notifier.notify(entry, "published " + entry.absolutePath() + " (ignored, is temporary)");
      return;
    }

    if (Files.exists(entry.absolutePath())) {
      if (!this.test) {
        boolean success = this.delete(entry.absolutePath());
        if (!success) {
          this.notifier.notify(entry, "deleted   " + entry.absolutePath() + " (failed)");
        }
      }
      this.notifier.notify(
          entry, "deleted   " + entry.absolutePath() + (this.test ? " (test)" : ""));
    }

    try {
      if (!this.test) {
        this.publisher.publish(entry.getObject());
      }
      this.notifier.notify(
          entry, "publish   " + entry.absolutePath() + (this.test ? " (test)" : ""));
    } catch (Throwable t) {
      this.notifier.notify(
          entry, "publish   " + entry.absolutePath() + " (failed: " + t.getMessage() + ")", t);
    }
  }

  private void processIllegalFileCollisions(ResultEntry entry) throws IOException {
    try {
      if (!this.test) {
        this.publisher.depublish(entry.getObject());
      }
      this.notifier.notify(
          entry, "depublish   " + entry.absolutePath() + (this.test ? " (test)" : ""));
    } catch (Throwable t) {
      this.notifier.notify(
          entry, "depublish   " + entry.absolutePath() + " (failed: " + t.getMessage() + ")", t);
    }
  }
}
