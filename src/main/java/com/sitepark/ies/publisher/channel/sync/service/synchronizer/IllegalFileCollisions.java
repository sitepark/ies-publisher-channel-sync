package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;

public class IllegalFileCollisions implements Syncronizer {

  @Override
  public void syncronize(SyncronizeContext ctx, ResultEntry entry) {
    if (entry.getResultType() != ResultType.ILLEGAL_FILENAME_COLLISION) {
      return;
    }

    try {
      if (!ctx.isTest()) {
        ctx.getPublisher().depublish(entry.getObject());
      }
      ctx.getNotifier()
          .notify(entry, "depublish " + entry.getAbsolutePath() + (ctx.isTest() ? " (test)" : ""));
    } catch (Throwable t) {
      ctx.getNotifier()
          .notify(
              entry,
              "depublish " + entry.getAbsolutePath() + " (failed: " + t.getMessage() + ")",
              t);
    }
  }
}
