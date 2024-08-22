package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserContext;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import com.sitepark.ies.publisher.channel.sync.service.Channel;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class MissingFileTest extends AnalyserTest {

  private final MissingFile analyser = new MissingFile();

  private final Path resourceDir = Path.of("src/test/resources/service/analyser/MissingFileTest");

  @Test
  void testWithNonEqualsPublicationType() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    when(ctx.getPublicationType()).thenReturn(PublicationType.OBJECT);

    Publication publication = mock();
    when(publication.type()).thenReturn(PublicationType.CONFIG);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, publication), "Should return OK");
  }

  @Test
  void testWithExistsDirectoryEntry() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    when(ctx.getPublicationType()).thenReturn(PublicationType.OBJECT);
    when(ctx.hasDirectoryEntry(any())).thenReturn(true);

    Publication publication = mock();
    when(publication.type()).thenReturn(PublicationType.OBJECT);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, publication), "Should return OK");
  }

  @Test
  void testWithCollision() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    when(ctx.getPublicationType()).thenReturn(PublicationType.OBJECT);
    when(ctx.hasDirectoryEntry(anyString())).thenReturn(false);

    Publication publication = mock();
    when(publication.type()).thenReturn(PublicationType.OBJECT);
    when(publication.isCollision()).thenReturn(true);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, publication), "Should return OK");
  }

  @Test
  void testNonPublished() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    when(ctx.getPublicationType()).thenReturn(PublicationType.OBJECT);
    when(ctx.hasDirectoryEntry(anyString())).thenReturn(false);

    Publication publication = mock();
    when(publication.type()).thenReturn(PublicationType.OBJECT);
    when(publication.isCollision()).thenReturn(false);
    when(publication.isPublished()).thenReturn(false);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, publication), "Should return OK");
  }

  @Test
  void testWithEmptyPath() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    when(ctx.getPublicationType()).thenReturn(PublicationType.OBJECT);
    when(ctx.hasDirectoryEntry(anyString())).thenReturn(false);

    Publication publication = mock();
    when(publication.type()).thenReturn(PublicationType.OBJECT);
    when(publication.isCollision()).thenReturn(false);
    when(publication.isPublished()).thenReturn(true);
    when(publication.path()).thenReturn(Path.of(""));

    AnalyserResult expected =
        ctx.getAnalyserResultFactory()
            .createInterruptResult(ResultType.LOST_PUBLICATION, publication);
    assertEquals(expected, this.analyser.analyse(ctx, publication), "Unexpected result");
  }

  @Test
  void testIsRegulareFile() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    when(ctx.getPublicationType()).thenReturn(PublicationType.OBJECT);
    when(ctx.hasDirectoryEntry(anyString())).thenReturn(false);

    Path regulareFile = this.resourceDir.resolve("regulare-file");
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.resolve(any(PublicationType.class), any(Path.class))).thenReturn(regulareFile);

    Publication publication = mock();
    when(publication.type()).thenReturn(PublicationType.OBJECT);
    when(publication.isCollision()).thenReturn(false);
    when(publication.isPublished()).thenReturn(true);
    when(publication.path()).thenReturn(Path.of("regulare-file"));

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, publication), "Should return OK");
  }

  @Test
  void testMissingFile() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    when(ctx.getPublicationType()).thenReturn(PublicationType.OBJECT);
    when(ctx.hasDirectoryEntry(anyString())).thenReturn(false);

    Path regulareFile = this.resourceDir.resolve("missing-file");
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.resolve(any(PublicationType.class), any(Path.class))).thenReturn(regulareFile);

    Publication publication = mock();
    when(publication.type()).thenReturn(PublicationType.OBJECT);
    when(publication.isCollision()).thenReturn(false);
    when(publication.isPublished()).thenReturn(true);
    when(publication.path()).thenReturn(Path.of("missing-file-file"));

    AnalyserResult expected =
        ctx.getAnalyserResultFactory().createInterruptResult(ResultType.MISSING_FILE, publication);
    assertEquals(expected, this.analyser.analyse(ctx, publication), "Unexpected result");
  }
}
