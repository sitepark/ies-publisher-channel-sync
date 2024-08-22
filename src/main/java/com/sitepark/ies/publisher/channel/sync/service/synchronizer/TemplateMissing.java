package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;

public class TemplateMissing implements Syncronizer {

  @Override
  public void syncronize(SyncronizeContext ctx, ResultEntry entry) {
    if (entry.getResultType() != ResultType.TEMPLATE_MISSING) {
      return;
    }

    ctx.getNotifier()
        .notify(entry, "published " + entry.getAbsolutePath() + " (ignored, template missing)");
  }
}
