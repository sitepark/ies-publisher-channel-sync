package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.service.Channel;
import org.junit.jupiter.api.Test;

class DocumentRootLayoutFilesTest extends AnalyserTestBase {

  private final DocumentRootLayoutFiles analyser = new DocumentRootLayoutFiles();

  @Test
  void testNonDocumentRootLayout() {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    when(channel.layout()).thenReturn(ChannelLayout.RESOURCES);

    PublishedPath path = mock();

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWEBIES() {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    when(channel.layout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("WEB-IES");

    assertEquals(
        AnalyserResult.OK_AND_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK and interrupt");
  }

  @Test
  void testAliasMap() {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    when(channel.layout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);
    when(path.baseName()).thenReturn("aliases.map");

    assertEquals(
        AnalyserResult.OK_AND_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK and interrupt");
  }

  @Test
  void testRedirectMap() {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    when(channel.layout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);
    when(path.baseName()).thenReturn("redirects.map");

    assertEquals(
        AnalyserResult.OK_AND_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK and interrupt");
  }

  @Test
  void testOtherFile() {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    when(channel.layout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);
    when(path.baseName()).thenReturn("abc");

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testOtherDirectory() {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    when(channel.layout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("abc");

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }
}
