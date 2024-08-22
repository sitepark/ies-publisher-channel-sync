package com.sitepark.ies.publisher.channel.sync.usecase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.SyncronizeContext;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.Syncronizer;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class SyncronizeTest {

  @Test
  void testSynchronize() throws IOException {

    SyncronizeContext ctx = mock();
    Syncronizer syncronizer = mock();

    Syncronize syncronize = new Syncronize(ctx, Arrays.asList(syncronizer));

    ResultEntry entry = mock();
    AnalyserResult result = new AnalyserResult(Arrays.asList(entry), false);

    syncronize.syncronize(result);

    verify(syncronizer).syncronize(ctx, entry);
  }
}
