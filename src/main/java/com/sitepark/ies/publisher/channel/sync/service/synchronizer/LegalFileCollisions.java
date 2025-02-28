package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;

public class LegalFileCollisions implements Synchronizer {

  @Override
  public void synchronize(SynchronizeContext ctx, ResultEntry entry) {
    if (entry.getResultType() != ResultType.LEGAL_FILENAME_COLLISION) {
      return;
    }

    if (ctx.isNotifyLegalCollisions()) {
      ctx.getNotifier().notify(entry, "legal collision " + entry.getAbsolutePath() + " (skip)");
    }
  }
}
