package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;

public interface Syncronizer {
  void syncronize(SyncronizeContext ctx, ResultEntry entry);
}
