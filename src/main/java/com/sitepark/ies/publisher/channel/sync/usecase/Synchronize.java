package com.sitepark.ies.publisher.channel.sync.usecase;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.port.Publisher;
import com.sitepark.ies.publisher.channel.sync.port.SyncNotifier;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.FileDirectoryMismatch;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.IllegalFileCollisions;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.LegalFileCollisions;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.MissingOrInvalidFile;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.SynchronizeContext;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.Synchronizer;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.TemplateMissing;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.UnknownFileOrDirectory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class Synchronize {

  private final SynchronizeContext ctx;

  private final List<Synchronizer> synchronizer = new ArrayList<>();

  public Synchronize(
      boolean test,
      boolean deleteForce,
      boolean notifyLegalCollisions,
      Publisher publisher,
      SyncNotifier notifier) {
    this(
        SynchronizeContext.builder()
            .test(test)
            .deleteForce(deleteForce)
            .notifyLegalCollisions(notifyLegalCollisions)
            .publisher(publisher)
            .notifier(notifier)
            .build());
  }

  protected Synchronize(SynchronizeContext ctx) {
    this(
        ctx,
        Arrays.asList(
            new UnknownFileOrDirectory(),
            new FileDirectoryMismatch(),
            new IllegalFileCollisions(),
            new LegalFileCollisions(),
            new MissingOrInvalidFile(),
            new TemplateMissing()));
  }

  protected Synchronize(SynchronizeContext ctx, List<Synchronizer> synchronizer) {
    this.ctx = ctx;
    this.synchronizer.addAll(synchronizer);
  }

  public void synchronize(AnalyserResult result) {
    result.entries().forEach(this::synchronize);
  }

  private void synchronize(ResultEntry entry) {
    this.synchronizer.forEach(synchronize -> synchronize.synchronize(this.ctx, entry));
  }
}
