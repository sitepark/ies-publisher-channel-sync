package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.service.Channel;
import java.nio.file.Path;
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

    Path file = Path.of("/var/www/example.com/www/WEB-IES/modulea/ies-module.xml");

    when(channel.layout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);
    when(channel.relativize(any(), any())).thenReturn(Path.of("WEB-IES/modulea/ies-module.xml"));

    PublishedPath path =
        new PublishedPath(PublicationType.OBJECT, file, file.getFileName().toString());

    assertEquals(
        AnalyserResult.OK_AND_RECURSIVE_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK and interrupt");
  }

  @Test
  void testAliasMap() {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    Path file = Path.of("/var/www/example.com/www/aliases.map");

    when(channel.layout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);
    when(channel.relativize(any(), any())).thenReturn(file.getFileName());

    PublishedPath path =
        new PublishedPath(PublicationType.OBJECT, file, file.getFileName().toString());

    assertEquals(
        AnalyserResult.OK_AND_RECURSIVE_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK and interrupt");
  }

  @Test
  void testRedirectMap() {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    Path file = Path.of("/var/www/example.com/www/redirects.map");

    when(channel.layout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);
    when(channel.relativize(any(), any())).thenReturn(file.getFileName());

    PublishedPath path =
        new PublishedPath(PublicationType.OBJECT, file, file.getFileName().toString());

    assertEquals(
        AnalyserResult.OK_AND_RECURSIVE_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK and interrupt");
  }

  @Test
  void testOtherFile() {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    Path file = Path.of("/var/www/example.com/www/abc");

    when(channel.layout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);
    when(channel.relativize(any(), any())).thenReturn(file.getFileName());

    PublishedPath path =
        new PublishedPath(PublicationType.OBJECT, file, file.getFileName().toString());

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testOtherDirectory() {

    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);

    Path file = Path.of("/var/www/example.com/www/abc");

    when(channel.layout()).thenReturn(ChannelLayout.DOCUMENT_ROOT);
    when(channel.relativize(any(), any())).thenReturn(file.getFileName());

    PublishedPath path =
        new PublishedPath(PublicationType.OBJECT, file, file.getFileName().toString());

    PublishedPath mockedPath = spy(path);
    when(mockedPath.isDirectory()).thenReturn(true);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }
}
