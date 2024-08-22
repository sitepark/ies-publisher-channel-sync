package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import com.sitepark.ies.publisher.channel.sync.service.Channel;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class PublicationTypeMismatchTest extends AnalyserTest {

  private final PublicationTypeMismatch analyser = new PublicationTypeMismatch();

  @Test
  void testWhenPublishedPathIsDirectory() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWhenChannelLayoutIsNotResources() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.getLayout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWithMismatch() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.getLayout()).thenReturn(ChannelLayout.RESOURCES);

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);
    when(path.baseName()).thenReturn("baseName");
    when(path.type()).thenReturn(PublicationType.OBJECT);

    Publication a = mock();
    when(a.isPublished()).thenReturn(false);

    Publication b = mock();
    when(b.isPublished()).thenReturn(true);
    when(b.type()).thenReturn(PublicationType.OBJECT);

    Publication c = mock();
    when(c.isPublished()).thenReturn(true);
    when(c.type()).thenReturn(PublicationType.CONFIG);

    when(directory.getPublications("baseName")).thenReturn(Arrays.asList(a, b, c));

    AnalyserResult expected =
        ctx.getAnalyserResultFactory().createResult(ResultType.PUBLICATION_TYPE_MISMATCH, c);
    assertEquals(expected, this.analyser.analyse(ctx, path), "Unexpected result");
  }
}
