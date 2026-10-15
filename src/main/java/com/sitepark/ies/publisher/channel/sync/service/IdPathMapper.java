package com.sitepark.ies.publisher.channel.sync.service;

import java.nio.file.Path;

public class IdPathMapper {
  private final IdPathStrategy strategy;

  public IdPathMapper(IdPathStrategy strategy) {
    this.strategy = java.util.Objects.requireNonNull(strategy, "strategy");
  }

  public Path pathFor(long id) {
    return strategy.toPath(id);
  }
}
