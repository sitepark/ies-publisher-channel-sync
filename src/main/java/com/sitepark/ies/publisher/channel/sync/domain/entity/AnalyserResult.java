package com.sitepark.ies.publisher.channel.sync.domain.entity;

import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntry;
import java.util.Collections;
import java.util.List;

public record AnalyserResult(
    List<ResultEntry> entries, boolean interrupt, boolean recursiveInterrupt) {

  public static final AnalyserResult OK = new AnalyserResult(Collections.emptyList(), false, false);

  public static final AnalyserResult OK_AND_RECURSIVE_INTERRUPT =
      new AnalyserResult(Collections.emptyList(), true, true);

  public static final AnalyserResult OK_AND_INTERRUPT =
      new AnalyserResult(Collections.emptyList(), true, false);

  @SuppressWarnings("PMD.UnusedAssignment")
  public AnalyserResult {
    entries = Collections.unmodifiableList(entries);
  }
}
