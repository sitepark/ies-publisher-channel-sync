package com.sitepark.ies.publisher.channel.sync.service;

import java.nio.file.Path;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface IdPathStrategy {
  Path toPath(long id);
}
