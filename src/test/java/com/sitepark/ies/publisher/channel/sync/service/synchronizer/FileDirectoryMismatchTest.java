package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import com.sitepark.ies.publisher.channel.sync.port.Publisher;
import com.sitepark.ies.publisher.channel.sync.port.SyncNotifier;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"PMD.JUnitTestContainsTooManyAsserts", "PMD.AvoidDuplicateLiterals"})
class FileDirectoryMismatchTest {

  private final Publisher publisher = mock();

  private final SyncNotifier notifier = mock();

  private SyncronizeContext ctx;

  private final Syncronizer syncronizer = new FileDirectoryMismatch();

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
  void testWithDeleteNotSuccessfull() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.FILE_DIRECTORY_MISMATCH);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(this.ctx.delete(any())).thenReturn(false);

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.notifier, times(1)).notify(entry, "deleted /a/b/c (failed)");
  }

  @Test
  void testWithDeleteAndRepublish() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.FILE_DIRECTORY_MISMATCH);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(this.ctx.delete(any())).thenReturn(true);

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.publisher).republish(any());
    verify(this.notifier, times(2)).notify(any(), any());
    verify(this.notifier).notify(entry, "deleted /a/b/c");
    verify(this.notifier).notify(entry, "republish /a/b/c");
  }

  @Test
  void testWithDeleteAndRepublishInTestMode() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.FILE_DIRECTORY_MISMATCH);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(this.ctx.delete(any())).thenReturn(true);
    when(this.ctx.isTest()).thenReturn(true);

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.publisher, never()).republish(any());
    verify(this.notifier, times(2)).notify(any(), any());
    verify(this.notifier).notify(entry, "deleted /a/b/c (test)");
    verify(this.notifier).notify(entry, "republish /a/b/c (test)");
  }

  @Test
  void testWithDeleteAndRepublishFailed() throws IOException {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.FILE_DIRECTORY_MISMATCH);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(this.ctx.delete(any())).thenReturn(true);
    when(this.ctx.isTest()).thenReturn(false);
    Throwable t = new RuntimeException("test");
    doThrow(t).when(this.publisher).republish(any());

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.publisher, times(1)).republish(any());
    verify(this.notifier, times(1)).notify(any(), any());
    verify(this.notifier, times(1)).notify(any(), any(), any());
    verify(this.notifier).notify(entry, "deleted /a/b/c");
    verify(this.notifier).notify(entry, "republish /a/b/c (failed: test)", t);
  }
}
