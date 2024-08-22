package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserContext;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class EmbeddedMediaDirectoryTest extends AnalyserTest {

  private final Path resourceDir =
      Path.of("src/test/resources/service/analyser/EmbeddedMediaDirectoryTest");

  private final EmbeddedMediaDirectory analyser = new EmbeddedMediaDirectory();

  @Test
  void testWhenPublishedPathIsNotADirectory() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testNoDotMediaSuffix() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("normal-directory");

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testInvalidParentPath() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("test.php.media");
    when(path.absolutePath()).thenReturn(Path.of("test.php.media"));

    assertThrows(IllegalArgumentException.class, () -> this.analyser.analyse(ctx, path));
  }

  @Test
  void testWithPublication() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("test.php.media");
    when(path.absolutePath()).thenReturn(Path.of("/root/test.php.media"));

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    Publication a = mock();
    when(a.isPublished()).thenReturn(false);
    Publication b = mock();
    when(b.isPublished()).thenReturn(true);
    when(directory.getPublications("test.php")).thenReturn(Arrays.asList(a, b));

    assertEquals(
        AnalyserResult.OK_AND_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK_AND_INTERRUPT");
  }

  @Test
  void testWithoutPublication() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("test.php.media");
    when(path.absolutePath()).thenReturn(Path.of("/root/test.php.media"));

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    AnalyserResult result = this.analyser.analyse(ctx, path);

    AnalyserResult expected =
        ctx.getAnalyserResultFactory()
            .createInterruptResultDeleteForce(ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);

    assertEquals(expected, result, "Unexpected result");
  }

  @Test
  void testPublicationWithoutMediaIdDir() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    Publication publication = mock();
    when(publication.absolutePath()).thenReturn(Path.of("test.php"));

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, publication), "Should return OK");
  }

  @Test
  void testPublicationWithoutMediaDir() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    Publication publication = mock();
    when(publication.absolutePath()).thenReturn(Path.of("b/test.php"));

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, publication), "Should return OK");
  }

  @Test
  void testPublicationWithoutMediaSuffixDir() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    Publication publication = mock();
    when(publication.absolutePath()).thenReturn(Path.of("a/b/test.php"));

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, publication), "Should return OK");
  }

  @Test
  void testWithExistsPublication() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    Publication publication = mock();
    when(publication.absolutePath())
        .thenReturn(this.resourceDir.resolve("test.php.media/123/image.png").toAbsolutePath());

    assertEquals(
        AnalyserResult.OK_AND_INTERRUPT,
        this.analyser.analyse(ctx, publication),
        "Should return OK_AND_INTERRUPT");
  }

  @Test
  void testWithNonExistsPublication() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    Publication publication = mock();
    when(publication.absolutePath())
        .thenReturn(
            this.resourceDir.resolve("test.php.media/123/non-existsimage.png").toAbsolutePath());

    AnalyserResult result = this.analyser.analyse(ctx, publication);

    AnalyserResult expected =
        ctx.getAnalyserResultFactory().createInterruptResult(ResultType.MISSING_FILE, publication);

    assertEquals(expected, result, "Unexpected result");
  }
}
