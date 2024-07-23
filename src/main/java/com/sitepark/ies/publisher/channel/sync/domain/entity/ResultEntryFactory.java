package com.sitepark.ies.publisher.channel.sync.domain.entity;

public class ResultEntryFactory {

  private final AnalyserContext ctx;

  public ResultEntryFactory(AnalyserContext ctx) {
    this.ctx = ctx;
  }

  public ResultEntry createResultEntry(ResultType type, Publication publication) {
    return this.builder().resultType(type).publication(publication).build();
  }

  public ResultEntry createResultEntry(ResultType type, PublishedPath path) {
    return this.builder().resultType(type).publishedPath(path).build();
  }

  public ResultEntry createResultEntryDeleteForce(ResultType type, PublishedPath path) {
    return this.builder().resultType(type).publishedPath(path).deleteForce(true).build();
  }

  private ResultEntry.Builder builder() {
    return ResultEntry.builder().iesDirectory(this.ctx.getIesDirectory());
  }
}
