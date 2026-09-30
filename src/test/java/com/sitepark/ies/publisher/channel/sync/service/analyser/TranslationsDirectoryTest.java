package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;
import com.sitepark.ies.publisher.channel.sync.service.Channel;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class TranslationsDirectoryTest extends AnalyserTestBase {

  private final TranslationsDirectory analyser = new TranslationsDirectory();

  private final Path resourceDir =
      Path.of("src/test/resources/service/analyser/TranslationsDirectoryTest");

  @Test
  void testWhenPublishedPathIsFile() {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWithDirectoryNonTranslationsSuffix() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.layout()).thenReturn(ChannelLayout.URL_BASED_RESOURCES);

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    Publication depublishedPublication = mock();
    when(depublishedPublication.isPublished()).thenReturn(false);
    Publication publishedPublication = mock();
    when(publishedPublication.isPublished()).thenReturn(true);
    when(directory.getPublications("baseName"))
        .thenReturn(Arrays.asList(depublishedPublication, publishedPublication));

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("baseName");

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWithExistsPublicationDirectoryUrlBased() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.layout()).thenReturn(ChannelLayout.URL_BASED_RESOURCES);

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    Publication depublishedPublication = mock();
    when(depublishedPublication.isPublished()).thenReturn(false);
    Publication publishedPublication = mock();
    when(publishedPublication.isPublished()).thenReturn(true);
    when(directory.getPublications("existsDir.php"))
        .thenReturn(Arrays.asList(depublishedPublication, publishedPublication));

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("existsDir.php.translations");

    assertEquals(
        AnalyserResult.OK_AND_RECURSIVE_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK and interrupt");
  }

  @Test
  void testWithExistsPublicationDirectoryIdBased() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.layout()).thenReturn(ChannelLayout.ID_BASED_RESOURCES);

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    Publication depublishedPublication = mock();
    when(depublishedPublication.isPublished()).thenReturn(false);
    Publication publishedPublication = mock();
    when(publishedPublication.isPublished()).thenReturn(true);
    when(directory.getPublications("123.php"))
        .thenReturn(Arrays.asList(depublishedPublication, publishedPublication));

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("123.translations");

    assertEquals(
        AnalyserResult.OK_AND_RECURSIVE_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK and interrupt");
  }

  @Test
  void testWithNonExistsPublicationDirectory() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.layout()).thenReturn(ChannelLayout.URL_BASED_RESOURCES);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("nonExistsDir.php.translations");
    when(path.absolutePath())
        .thenReturn(this.resourceDir.resolve("nonExistsDir.php.translations").toAbsolutePath());

    AnalyserResult expected =
        ctx.getAnalyserResultFactory()
            .createRecursiveInterruptResultDeleteForce(ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);
    assertEquals(expected, this.analyser.analyse(ctx, path), "Unexpected result");
  }
}
