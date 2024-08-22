package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;

public class MissingOrInvalidFile implements Syncronizer {

  @Override
  public void syncronize(SyncronizeContext ctx, ResultEntry entry) {

    if (entry.getResultType() != ResultType.HASH_MISMATCH
        && entry.getResultType() != ResultType.MISSING_FILE
        && entry.getResultType() != ResultType.LOST_PUBLICATION) {
      return;
    }

    if (entry.isTemporary()) {
      ctx.getNotifier()
          .notify(entry, "published " + entry.getAbsolutePath() + " (ignored, is temporary)");
      return;
    }

    this.delete(ctx, entry);
    this.publish(ctx, entry);
  }

  private void delete(SyncronizeContext ctx, ResultEntry entry) {

    if (!ctx.exists(entry.getAbsolutePath())) {
      return;
    }
    if (!ctx.isTest()) {
      boolean success = ctx.delete(entry.getAbsolutePath());
      if (!success) {
        ctx.getNotifier().notify(entry, "deleted " + entry.getAbsolutePath() + " (failed)");
      }
    }
    ctx.getNotifier()
        .notify(entry, "deleted " + entry.getAbsolutePath() + (ctx.isTest() ? " (test)" : ""));
  }

  private void publish(SyncronizeContext ctx, ResultEntry entry) {
    try {
      if (!ctx.isTest()) {
        ctx.getPublisher().publish(entry.getObject());
      }
      ctx.getNotifier()
          .notify(entry, "publish " + entry.getAbsolutePath() + (ctx.isTest() ? " (test)" : ""));
    } catch (Throwable t) {
      ctx.getNotifier()
          .notify(
              entry, "publish " + entry.getAbsolutePath() + " (failed: " + t.getMessage() + ")", t);
    }
  }
}
