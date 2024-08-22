package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class TranslationsDirectoryTest extends AnalyserTest {

  private final TranslationsDirectory analyser = new TranslationsDirectory();

  private final Path resourceDir =
      Path.of("src/test/resources/service/analyser/TranslationsDirectoryTest");

  @Test
  void testWhenPublishedPathIsFile() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWithDirectoryNonTranslationsSuffix() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

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
  void testWithExistsPublicationDirectory() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    Publication depublishedPublication = mock();
    when(depublishedPublication.isPublished()).thenReturn(false);
    Publication publishedPublication = mock();
    when(publishedPublication.isPublished()).thenReturn(true);
    when(directory.getPublications("existsDir"))
        .thenReturn(Arrays.asList(depublishedPublication, publishedPublication));

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("existsDir.translations");

    assertEquals(
        AnalyserResult.OK_AND_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK and iterrupt");
  }

  @Test
  void testWithNonExistsPublicationDirectory() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("nonExistsDir.translations");
    when(path.absolutePath())
        .thenReturn(this.resourceDir.resolve("nonExistsDir.translations").toAbsolutePath());

    AnalyserResult expected =
        ctx.getAnalyserResultFactory()
            .createInterruptResultDeleteForce(ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);
    assertEquals(expected, this.analyser.analyse(ctx, path), "Unexpected result");
  }
}
