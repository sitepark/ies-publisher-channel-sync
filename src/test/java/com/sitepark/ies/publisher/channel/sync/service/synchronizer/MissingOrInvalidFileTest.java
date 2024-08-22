package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

class MissingOrInvalidFileTest {

  private Publisher publisher = mock();

  private SyncNotifier notifier = mock();

  private SyncronizeContext ctx;

  private MissingOrInvalidFile syncronizer = new MissingOrInvalidFile();

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
  void testTemporaryEntry() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.MISSING_FILE);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(entry.isTemporary()).thenReturn(true);

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.notifier, times(1)).notify(entry, "published /a/b/c (ignored, is temporary)");
  }

  @Test
  void testWithTypeMissingFile() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.MISSING_FILE);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.notifier).notify(any(), any());
  }

  @Test
  void testWithTypeHashMismatch() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.HASH_MISMATCH);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.notifier).notify(any(), any());
  }

  @Test
  void testWithTypeLostPublication() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.LOST_PUBLICATION);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.notifier).notify(any(), any());
  }

  @Test
  void testWithDelete() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.MISSING_FILE);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(entry.getObject()).thenReturn(new Ref("1"));
    when(this.ctx.exists(any())).thenReturn(true);
    when(this.ctx.delete(any())).thenReturn(true);

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.ctx).delete(any());
    verify(this.notifier).notify(entry, "deleted /a/b/c");
    verify(this.publisher).publish(new Ref("1"));
    verify(this.notifier).notify(entry, "publish /a/b/c");
  }

  @Test
  void testWithDeleteFailed() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.MISSING_FILE);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(entry.getObject()).thenReturn(new Ref("1"));
    when(this.ctx.exists(any())).thenReturn(true);
    when(this.ctx.delete(any())).thenReturn(false);

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.ctx).delete(any());
    verify(this.notifier).notify(entry, "deleted /a/b/c (failed)");
    verify(this.publisher).publish(new Ref("1"));
    verify(this.notifier).notify(entry, "publish /a/b/c");
  }

  @Test
  void testWithDeleteInTestMode() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.MISSING_FILE);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(entry.getObject()).thenReturn(new Ref("1"));
    when(this.ctx.isTest()).thenReturn(true);
    when(this.ctx.exists(any())).thenReturn(true);
    when(this.ctx.delete(any())).thenReturn(true);

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.ctx, never()).delete(any());
    verify(this.notifier).notify(entry, "deleted /a/b/c (test)");
    verify(this.publisher, never()).publish(new Ref("1"));
    verify(this.notifier).notify(entry, "publish /a/b/c (test)");
  }

  @Test
  void testWithoutDelete() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.MISSING_FILE);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(entry.getObject()).thenReturn(new Ref("1"));
    when(this.ctx.exists(any())).thenReturn(false);

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.ctx, never()).delete(any());
    verify(this.notifier, times(1)).notify(any(), any());
    verify(this.publisher).publish(new Ref("1"));
    verify(this.notifier).notify(entry, "publish /a/b/c");
  }

  @Test
  void testPublishFailed() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.MISSING_FILE);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(entry.getObject()).thenReturn(new Ref("1"));
    when(this.ctx.exists(any())).thenReturn(true);
    Throwable t = new RuntimeException("test");
    doThrow(t).when(this.publisher).publish(any());

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.publisher).publish(new Ref("1"));
    verify(this.notifier).notify(entry, "publish /a/b/c (failed: test)", t);
  }
}
