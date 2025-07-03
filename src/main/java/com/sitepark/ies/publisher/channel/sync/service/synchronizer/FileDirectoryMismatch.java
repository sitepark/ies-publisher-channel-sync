package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;

public class FileDirectoryMismatch implements Synchronizer {

  @Override
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public void synchronize(SynchronizeContext ctx, ResultEntry entry) {

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
    } catch (Exception e) {
      ctx.getNotifier()
          .notify(
              entry,
              "republish " + entry.getAbsolutePath() + " (failed: " + e.getMessage() + ")",
              e);
    }
  }
}
