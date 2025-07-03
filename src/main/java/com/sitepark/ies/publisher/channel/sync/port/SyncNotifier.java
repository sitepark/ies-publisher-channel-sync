package com.sitepark.ies.publisher.channel.sync.port;

import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntry;

public interface SyncNotifier {
  void notify(ResultEntry entry, String message);

  void notify(ResultEntry entry, String message, Throwable t);
}
