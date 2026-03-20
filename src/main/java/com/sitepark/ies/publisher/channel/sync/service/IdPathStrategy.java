package com.sitepark.ies.publisher.channel.sync.service;

import java.nio.file.Path;

public interface IdPathStrategy {
  Path toPath(long id);
}
