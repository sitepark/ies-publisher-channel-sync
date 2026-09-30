package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;
import java.util.Objects;

public class MissingOrInvalidFile implements Synchronizer {

  @Override
  public void synchronize(SynchronizeContext ctx, ResultEntry entry) {

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

  private void delete(SynchronizeContext ctx, ResultEntry entry) {

    if (!ctx.exists(
        Objects.requireNonNull(entry.getAbsolutePath(), "absolutePath of the entry is null"))) {
      return;
    }
    if (!ctx.isTest()) {
      boolean success =
          ctx.delete(
              Objects.requireNonNull(entry.getAbsolutePath(), "absolutePath of the entry is null"));
      if (!success) {
        ctx.getNotifier().notify(entry, "deleted " + entry.getAbsolutePath() + " (failed)");
      }
    }
    ctx.getNotifier()
        .notify(entry, "deleted " + entry.getAbsolutePath() + (ctx.isTest() ? " (test)" : ""));
  }

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private void publish(SynchronizeContext ctx, ResultEntry entry) {
    try {
      if (!ctx.isTest()) {
        ctx.getPublisher()
            .publish(Objects.requireNonNull(entry.getObject(), "object of the entry is null"));
      }
      ctx.getNotifier()
          .notify(entry, "publish " + entry.getAbsolutePath() + (ctx.isTest() ? " (test)" : ""));
    } catch (Exception e) {
      ctx.getNotifier()
          .notify(
              entry, "publish " + entry.getAbsolutePath() + " (failed: " + e.getMessage() + ")", e);
    }
  }
}
