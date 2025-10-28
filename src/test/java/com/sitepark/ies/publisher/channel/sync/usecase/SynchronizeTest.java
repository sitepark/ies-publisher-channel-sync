package com.sitepark.ies.publisher.channel.sync.usecase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.SynchronizeContext;
import com.sitepark.ies.publisher.channel.sync.service.synchronizer.Synchronizer;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class SynchronizeTest {

  @Test
  void testSynchronize() {

    SynchronizeContext ctx = mock();
    Synchronizer synchronizer = mock();

    Synchronize synchronize = new Synchronize(ctx, Collections.singletonList(synchronizer));

    ResultEntry entry = mock();
    AnalyserResult result = new AnalyserResult(Collections.singletonList(entry), false, false);

    synchronize.synchronize(result);

    verify(synchronizer).synchronize(ctx, entry);
  }
}
