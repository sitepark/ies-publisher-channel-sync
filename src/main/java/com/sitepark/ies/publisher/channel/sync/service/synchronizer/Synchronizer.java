package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntry;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface Synchronizer {
  void synchronize(SynchronizeContext ctx, ResultEntry entry);
}
