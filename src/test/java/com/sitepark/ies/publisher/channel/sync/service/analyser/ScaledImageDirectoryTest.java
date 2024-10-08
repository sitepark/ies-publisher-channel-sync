package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import java.util.Collections;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class ScaledImageDirectoryTest extends AnalyserTest {

  private final ScaledImageDirectory analyser = new ScaledImageDirectory();

  @Test
  void testWhenPublishedPathIsFile() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWithDirectoryNonScaledSuffix() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("baseName");

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testPublicationDirectoryWithoutParent() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("baseName.scaled");
    when(path.absolutePath()).thenReturn(Path.of("baseName.scaled"));

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          this.analyser.analyse(ctx, path);
        });
  }

  @Test
  void testWithPublishedPublicationDirectory() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("baseName.scaled");
    when(path.absolutePath()).thenReturn(Path.of("/root/baseName.scaled"));

    Publication a = mock();
    when(a.isPublished()).thenReturn(false);

    Publication b = mock();
    when(b.isPublished()).thenReturn(true);

    when(directory.getPublications("baseName")).thenReturn(Arrays.asList(a, b));

    assertEquals(
        AnalyserResult.OK_AND_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK and iterrupt");
  }

  @Test
  void testWithNonPublishedPublicationDirectory() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("baseName.scaled");
    when(path.absolutePath()).thenReturn(Path.of("/root/baseName.scaled"));

    when(directory.getPublications("baseName")).thenReturn(Collections.emptyList());

    AnalyserResult expected =
        ctx.getAnalyserResultFactory()
            .createInterruptResultDeleteForce(ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);
    assertEquals(expected, this.analyser.analyse(ctx, path), "Unexpected result");
  }
}
