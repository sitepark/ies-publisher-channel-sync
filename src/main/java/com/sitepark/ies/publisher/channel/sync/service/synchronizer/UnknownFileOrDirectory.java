package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import java.nio.file.Path;

public class UnknownFileOrDirectory implements Syncronizer {

  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  @Override
  public void syncronize(SyncronizeContext ctx, ResultEntry entry) {

    if (entry.getResultType() != ResultType.UNKNOWN_FILE_OR_DIRECTORY) {
      return;
    }

    if (entry.isTemporary()) {
      ctx.getNotifier()
          .notify(entry, "deleted " + entry.getAbsolutePath() + " (ignored, is temporary)");
      return;
    }

    Path entryFile = entry.getAbsolutePath();

    boolean deleteDirectoryForce = entry.isDeleteForce() || ctx.isDeleteForce();
    if (ctx.isDirectory(entryFile) && !deleteDirectoryForce) {
      ctx.getNotifier()
          .notify(
              entry,
              "deleted " + entry.getAbsolutePath() + " (ignore, is directory, use --force-delete)");
      return;
    }

    boolean success = true;

    if (!ctx.isTest()) {
      success = ctx.delete(entryFile);
    }

    if (success) {
      ctx.getNotifier()
          .notify(entry, "deleted " + entry.getAbsolutePath() + (ctx.isTest() ? " (test)" : ""));
    } else {
      ctx.getNotifier().notify(entry, "deleted " + entry.getAbsolutePath() + " (failed)");
    }
  }
}
