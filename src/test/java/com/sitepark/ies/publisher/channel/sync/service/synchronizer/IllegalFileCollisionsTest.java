package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.Ref;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import com.sitepark.ies.publisher.channel.sync.port.Publisher;
import com.sitepark.ies.publisher.channel.sync.port.SyncNotifier;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
class IllegalFileCollisionsTest {

  private final Publisher publisher = mock();

  private final SyncNotifier notifier = mock();

  private SyncronizeContext ctx;

  private final Syncronizer syncronizer = new IllegalFileCollisions();

  @BeforeEach
  public void setup() {
    this.ctx = mock();
    when(this.ctx.getNotifier()).thenReturn(this.notifier);
    when(this.ctx.getPublisher()).thenReturn(this.publisher);
  }

  @Test
  void testWithInvalidResultType() throws IOException {

    ResultEntry entry = mock();

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.notifier, never()).notify(any(), any());
  }

  @Test
  void testDepublish() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.ILLEGAL_FILENAME_COLLISION);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(entry.getObject()).thenReturn(new Ref("1"));
    when(this.ctx.isTest()).thenReturn(false);

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.publisher).depublish(new Ref("1"));
    verify(this.notifier).notify(entry, "depublish /a/b/c");
  }

  @Test
  void testDepublishInTestMode() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.ILLEGAL_FILENAME_COLLISION);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(entry.getObject()).thenReturn(new Ref("1"));
    when(this.ctx.isTest()).thenReturn(true);

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.publisher, never()).depublish(any());
    verify(this.notifier).notify(entry, "depublish /a/b/c (test)");
  }

  @Test
  void testDepublishWithException() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.ILLEGAL_FILENAME_COLLISION);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(entry.getObject()).thenReturn(new Ref("1"));
    when(this.ctx.isTest()).thenReturn(false);
    Throwable t = new RuntimeException("test");
    doThrow(t).when(this.publisher).depublish(any());

    this.syncronizer.syncronize(this.ctx, entry);

    when(entry.getObject()).thenReturn(new Ref("1"));
    verify(this.notifier).notify(entry, "depublish /a/b/c (failed: test)", t);
  }
}
