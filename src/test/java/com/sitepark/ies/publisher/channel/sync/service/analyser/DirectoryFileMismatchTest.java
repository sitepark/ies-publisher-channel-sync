package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserContext;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class DirectoryFileMismatchTest extends AnalyserTest {

  private DirectoryFileMismatch analyser = new DirectoryFileMismatch();

  @Test
  void testWhenPublishedPathIsNotADirectory() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWithPublishedPublication() throws IOException {
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

    AnalyserResult result = this.analyser.analyse(ctx, path);

    AnalyserResult expected =
        ctx.getAnalyserResultFactory()
            .createResult(ResultType.FILE_DIRECTORY_MISMATCH, publishedPublication);

    assertEquals(expected, result, "Unexpected result");
  }

  @Test
  void testWithDepublishedPublication() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    Publication publication = mock();
    when(directory.getPublications("baseName")).thenReturn(Arrays.asList(publication));
    when(publication.isPublished()).thenReturn(false);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("baseName");

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWithoutPublication() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("baseName");

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }
}
