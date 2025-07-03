package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;
import com.sitepark.ies.publisher.channel.sync.port.Publisher;
import com.sitepark.ies.publisher.channel.sync.port.SyncNotifier;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LegalFileCollisionsTest {

  private final Publisher publisher = mock();

  private final SyncNotifier notifier = mock();
  private final Synchronizer synchronize = new LegalFileCollisions();
  private SynchronizeContext ctx;

  @BeforeEach
  public void setup() {
    this.ctx = mock();
    when(this.ctx.getNotifier()).thenReturn(this.notifier);
    when(this.ctx.getPublisher()).thenReturn(this.publisher);
  }

  @Test
  void testWithInvalidResultType() {

    ResultEntry entry = mock();

    this.synchronize.synchronize(this.ctx, entry);

    verify(this.notifier, never()).notify(any(), any());
  }

  @Test
  void testNotify() {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.LEGAL_FILENAME_COLLISION);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(this.ctx.isNotifyLegalCollisions()).thenReturn(true);

    this.synchronize.synchronize(this.ctx, entry);

    verify(this.notifier).notify(entry, "legal collision /a/b/c (skip)");
  }

  @Test
  void testNotNotify() {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.LEGAL_FILENAME_COLLISION);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(this.ctx.isNotifyLegalCollisions()).thenReturn(false);

    this.synchronize.synchronize(this.ctx, entry);

    verify(this.notifier, never()).notify(any(), any());
  }
}
