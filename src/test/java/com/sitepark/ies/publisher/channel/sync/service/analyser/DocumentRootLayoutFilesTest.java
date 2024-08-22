package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.service.Channel;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class DocumentRootLayoutFilesTest extends AnalyserTest {

  private final DocumentRootLayoutFiles analyser = new DocumentRootLayoutFiles();

  @Test
  void testNonDocumentRootLayout() throws IOException {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    when(channel.getLayout()).thenReturn(ChannelLayout.RESOURCES);

    PublishedPath path = mock();

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWEBIES() throws IOException {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    when(channel.getLayout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("WEB-IES");

    assertEquals(
        AnalyserResult.OK_AND_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK and iterrupt");
  }

  @Test
  void testAliasMap() throws IOException {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    when(channel.getLayout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);
    when(path.baseName()).thenReturn("aliases.map");

    assertEquals(
        AnalyserResult.OK_AND_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK and iterrupt");
  }

  @Test
  void testRedirectMap() throws IOException {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    when(channel.getLayout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);
    when(path.baseName()).thenReturn("redirects.map");

    assertEquals(
        AnalyserResult.OK_AND_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK and iterrupt");
  }

  @Test
  void testOtherFile() throws IOException {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    when(channel.getLayout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);
    when(path.baseName()).thenReturn("abc");

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testOtherDirectory() throws IOException {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    when(channel.getLayout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("abc");

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }
}
