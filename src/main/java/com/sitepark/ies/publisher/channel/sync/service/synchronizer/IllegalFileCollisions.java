package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;
import java.util.Objects;

public class IllegalFileCollisions implements Synchronizer {

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  @Override
  public void synchronize(SynchronizeContext ctx, ResultEntry entry) {
    if (entry.getResultType() != ResultType.ILLEGAL_FILENAME_COLLISION) {
      return;
    }

    try {
      if (!ctx.isTest()) {
        ctx.getPublisher()
            .depublish(Objects.requireNonNull(entry.getObject(), "object of the entry is null"));
      }
      ctx.getNotifier()
          .notify(entry, "depublish " + entry.getAbsolutePath() + (ctx.isTest() ? " (test)" : ""));
    } catch (Exception e) {
      ctx.getNotifier()
          .notify(
              entry,
              "depublish " + entry.getAbsolutePath() + " (failed: " + e.getMessage() + ")",
              e);
    }
  }
}
