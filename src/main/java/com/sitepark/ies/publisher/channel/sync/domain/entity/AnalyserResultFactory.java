package com.sitepark.ies.publisher.channel.sync.domain.entity;

import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntryFactory;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;
import java.util.Collections;
import java.util.List;

public class AnalyserResultFactory {
  private final ResultEntryFactory resultEntryFactory;

  public AnalyserResultFactory(ResultEntryFactory resultEntryFactory) {
    this.resultEntryFactory = resultEntryFactory;
  }

  public AnalyserResult createResult(ResultType type, Publication publication) {
    ResultEntry entry = this.resultEntryFactory.createResultEntry(type, publication);
    return new AnalyserResult(Collections.singletonList(entry), false, false);
  }

  public AnalyserResult createResult(ResultType type, PublishedPath path) {
    ResultEntry entry = this.resultEntryFactory.createResultEntry(type, path);
    return new AnalyserResult(Collections.singletonList(entry), false, false);
  }

  public AnalyserResult createRecursiveInterruptResult(ResultType type, Publication publication) {
    ResultEntry entry = this.resultEntryFactory.createResultEntry(type, publication);
    return new AnalyserResult(Collections.singletonList(entry), true, true);
  }

  public AnalyserResult createRecursiveInterruptResult(ResultType type, PublishedPath path) {
    ResultEntry entry = this.resultEntryFactory.createResultEntry(type, path);
    return new AnalyserResult(Collections.singletonList(entry), true, true);
  }

  public AnalyserResult createRecursiveInterruptResultDeleteForce(
      ResultType type, PublishedPath path) {
    ResultEntry entry = this.resultEntryFactory.createResultEntryDeleteForce(type, path);
    return new AnalyserResult(Collections.singletonList(entry), true, true);
  }

  public AnalyserResult createResult(List<ResultEntry> entries) {
    return new AnalyserResult(entries, false, false);
  }

  public AnalyserResult createRecursiveInterruptResult(List<ResultEntry> entries) {
    return new AnalyserResult(entries, true, true);
  }
}
