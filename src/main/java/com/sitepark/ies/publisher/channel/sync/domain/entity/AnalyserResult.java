package com.sitepark.ies.publisher.channel.sync.domain.entity;

import java.util.Collections;
import java.util.List;

public record AnalyserResult(List<ResultEntry> entries, boolean interrupt) {

  public static final AnalyserResult OK = new AnalyserResult(Collections.emptyList(), false);

  public static final AnalyserResult OK_AND_INTERRUPT =
      new AnalyserResult(Collections.emptyList(), true);

  @SuppressWarnings("PMD.UnusedAssignment")
  public AnalyserResult {
    entries = Collections.unmodifiableList(entries);
  }
}
