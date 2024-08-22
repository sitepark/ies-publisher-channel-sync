package com.sitepark.ies.publisher.channel.sync.usecase;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.port.Publisher;
import com.sitepark.ies.publisher.channel.sync.port.SyncNotifier;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.FileDirectoryMismatch;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.IllegalFileCollisions;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.LegalFileCollisions;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.MissingOrInvalidFile;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.SyncronizeContext;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.Syncronizer;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.TemplateMissing;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.UnknownFileOrDirectory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class Syncronize {

  private final SyncronizeContext ctx;

  private final List<Syncronizer> syncronizers = new ArrayList<>();

  public Syncronize(
      boolean test,
      boolean deleteForce,
      boolean notifyLegalCollisions,
      Publisher publisher,
      SyncNotifier notifier) {
    this(
        SyncronizeContext.builder()
            .test(test)
            .deleteForce(deleteForce)
            .notifyLegalCollisions(notifyLegalCollisions)
            .publisher(publisher)
            .notifier(notifier)
            .build());
  }

  protected Syncronize(SyncronizeContext ctx) {
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

  protected Syncronize(SyncronizeContext ctx, List<Syncronizer> syncronizers) {
    this.ctx = ctx;
    this.syncronizers.addAll(syncronizers);
  }

  public void syncronize(AnalyserResult result) throws IOException {
    result.entries().forEach(this::syncronize);
  }

  private void syncronize(ResultEntry entry) {
    this.syncronizers.forEach(syncronizer -> syncronizer.syncronize(this.ctx, entry));
  }
}
