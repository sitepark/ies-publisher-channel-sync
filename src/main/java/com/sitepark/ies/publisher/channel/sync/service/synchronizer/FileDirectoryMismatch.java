package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;

public class FileDirectoryMismatch implements Syncronizer {

  @Override
  public void syncronize(SyncronizeContext ctx, ResultEntry entry) {

    if (entry.getResultType() != ResultType.FILE_DIRECTORY_MISMATCH) {
      return;
    }

    boolean success = true;
    if (!ctx.isTest()) {
      success = ctx.delete(entry.getAbsolutePath());
    }

    if (!success) {
      ctx.getNotifier().notify(entry, "deleted " + entry.getAbsolutePath() + " (failed)");
      return;
    }

    ctx.getNotifier()
        .notify(entry, "deleted " + entry.getAbsolutePath() + (ctx.isTest() ? " (test)" : ""));
    try {
      if (!ctx.isTest()) {
        ctx.getPublisher().republish(entry.getObject());
      }
      ctx.getNotifier()
          .notify(entry, "republish " + entry.getAbsolutePath() + (ctx.isTest() ? " (test)" : ""));
    } catch (Throwable t) {
      ctx.getNotifier()
          .notify(
              entry,
              "republish " + entry.getAbsolutePath() + " (failed: " + t.getMessage() + ")",
              t);
    }
  }
}
