package com.sitepark.ies.publisher.channel.sync.port;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;

public interface SyncNotifier {
  public void notify(ResultEntry entry, String message);

  public void notify(ResultEntry entry, String message, Throwable t);
}
