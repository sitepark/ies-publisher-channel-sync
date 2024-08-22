package com.sitepark.ies.publisher.channel.sync.service.synchronizer;

import static org.mockito.ArgumentMatchers.any;
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

class TemplateMissingTest {

  private Publisher publisher = mock();

  private SyncNotifier notifier = mock();

  private SyncronizeContext ctx;

  private TemplateMissing syncronizer = new TemplateMissing();

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
    when(entry.getResultType()).thenReturn(ResultType.TEMPLATE_MISSING);
    when(entry.getAbsolutePath()).thenReturn(Path.of("/a/b/c"));

    this.syncronizer.syncronize(this.ctx, entry);

    verify(this.notifier, times(1)).notify(entry, "published /a/b/c (ignored, template missing)");
  }
}
