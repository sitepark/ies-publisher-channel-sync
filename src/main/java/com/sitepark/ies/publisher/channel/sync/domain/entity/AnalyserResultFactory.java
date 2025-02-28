package com.sitepark.ies.publisher.channel.sync.domain.entity;

import java.util.Collections;
import java.util.List;

public class AnalyserResultFactory {
  private final ResultEntryFactory resultEntryFactory;

  public AnalyserResultFactory(ResultEntryFactory resultEntryFactory) {
    this.resultEntryFactory = resultEntryFactory;
  }

  public AnalyserResult createResult(ResultType type, Publication publication) {
    ResultEntry entry = this.resultEntryFactory.createResultEntry(type, publication);
    return new AnalyserResult(Collections.singletonList(entry), false);
  }

  public AnalyserResult createResult(ResultType type, PublishedPath path) {
    ResultEntry entry = this.resultEntryFactory.createResultEntry(type, path);
    return new AnalyserResult(Collections.singletonList(entry), false);
  }

  public AnalyserResult createInterruptResult(ResultType type, Publication publication) {
    ResultEntry entry = this.resultEntryFactory.createResultEntry(type, publication);
    return new AnalyserResult(Collections.singletonList(entry), true);
  }

  public AnalyserResult createInterruptResult(ResultType type, PublishedPath path) {
    ResultEntry entry = this.resultEntryFactory.createResultEntry(type, path);
    return new AnalyserResult(Collections.singletonList(entry), true);
  }

  public AnalyserResult createInterruptResultDeleteForce(ResultType type, PublishedPath path) {
    ResultEntry entry = this.resultEntryFactory.createResultEntryDeleteForce(type, path);
    return new AnalyserResult(Collections.singletonList(entry), true);
  }

  public AnalyserResult createResult(List<ResultEntry> entries) {
    return new AnalyserResult(entries, false);
  }

  public AnalyserResult createInterruptResult(List<ResultEntry> entries) {
    return new AnalyserResult(entries, true);
  }
}
