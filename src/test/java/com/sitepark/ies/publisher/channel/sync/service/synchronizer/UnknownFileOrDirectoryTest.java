package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import com.sitepark.ies.publisher.channel.sync.port.Publisher;
import com.sitepark.ies.publisher.channel.sync.port.SyncNotifier;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({
  "PMD.UnitTestContainsTooManyAsserts",
  "PMD.AvoidDuplicateLiterals",
  "DuplicateExpressions"
})
class UnknownFileOrDirectoryTest {

  private final Publisher publisher = mock();

  private final SyncNotifier notifier = mock();
  private final Synchronizer synchronize = new UnknownFileOrDirectory();
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
  void testWithTemporaryEntry() {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.UNKNOWN_FILE_OR_DIRECTORY);
    when(entry.isTemporary()).thenReturn(true);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));

    this.synchronize.synchronize(this.ctx, entry);

    verify(this.notifier).notify(entry, "deleted /a/b/c (ignored, is temporary)");
  }

  @Test
  void testDeleteDirectoryNotForce() {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.UNKNOWN_FILE_OR_DIRECTORY);
    when(entry.isTemporary()).thenReturn(false);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(entry.isDeleteForce()).thenReturn(false);
    when(this.ctx.isDeleteForce()).thenReturn(false);
    when(this.ctx.isDirectory(any())).thenReturn(true);

    this.synchronize.synchronize(this.ctx, entry);

    verify(this.notifier)
        .notify(entry, "deleted /a/b/c (ignore, is directory, use --force-delete)");
  }

  @Test
  void testDeleteDirectoryForceDeleteByEntry() {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.UNKNOWN_FILE_OR_DIRECTORY);
    when(entry.isTemporary()).thenReturn(false);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(entry.isDeleteForce()).thenReturn(true);
    when(this.ctx.isTest()).thenReturn(false);
    when(this.ctx.isDeleteForce()).thenReturn(false);
    when(this.ctx.isDirectory(any())).thenReturn(true);
    when(this.ctx.delete(any())).thenReturn(true);

    this.synchronize.synchronize(this.ctx, entry);

    verify(this.ctx).delete(Path.of("/a/b/c"));
    verify(this.notifier).notify(entry, "deleted /a/b/c");
  }

  @Test
  void testDeleteDirectoryForceDeleteByContext() {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.UNKNOWN_FILE_OR_DIRECTORY);
    when(entry.isTemporary()).thenReturn(false);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(entry.isDeleteForce()).thenReturn(false);
    when(this.ctx.isTest()).thenReturn(false);
    when(this.ctx.isDeleteForce()).thenReturn(true);
    when(this.ctx.isDirectory(any())).thenReturn(true);
    when(this.ctx.delete(any())).thenReturn(true);

    this.synchronize.synchronize(this.ctx, entry);

    verify(this.ctx).delete(Path.of("/a/b/c"));
    verify(this.notifier).notify(entry, "deleted /a/b/c");
  }

  @Test
  void testDeleteTest() {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.UNKNOWN_FILE_OR_DIRECTORY);
    when(entry.isTemporary()).thenReturn(false);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(this.ctx.isTest()).thenReturn(true);
    when(this.ctx.isDirectory(any())).thenReturn(false);

    this.synchronize.synchronize(this.ctx, entry);

    verify(this.ctx, never()).delete(Path.of("/a/b/c"));
    verify(this.notifier).notify(entry, "deleted /a/b/c (test)");
  }

  @Test
  void testDeleteFailed() {

    ResultEntry entry = mock();
    when(entry.getResultType()).thenReturn(ResultType.UNKNOWN_FILE_OR_DIRECTORY);
    when(entry.isTemporary()).thenReturn(false);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));
    when(this.ctx.isTest()).thenReturn(false);
    when(this.ctx.isDirectory(any())).thenReturn(false);
    when(this.ctx.delete(any())).thenReturn(false);

    this.synchronize.synchronize(this.ctx, entry);

    verify(this.ctx).delete(Path.of("/a/b/c"));
    verify(this.notifier).notify(entry, "deleted /a/b/c (failed)");
  }
}
