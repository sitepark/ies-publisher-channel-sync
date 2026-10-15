package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;
import com.sitepark.ies.publisher.channel.sync.port.Hasher;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

@SuppressWarnings("DuplicateExpressions")
class HashMismatchTest extends AnalyserTestBase {

  private final HashMismatch analyser = new HashMismatch(new TestHasher());

  private final Path resourceDir = Path.of("src/test/resources/service/analyser/HashMismatchTest");

  @Test
  void testWhenPublishedPathIsDirectory() {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWithMatchedPublicationDirectory() {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);
    when(path.baseName()).thenReturn("baseName");

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    Publication a = mock();
    when(a.isPublished()).thenReturn(false);

    Publication b = mock();
    when(b.isPublished()).thenReturn(true);
    when(b.path()).thenReturn(Path.of(""));

    Publication c = mock();
    when(c.isPublished()).thenReturn(true);
    when(c.path()).thenReturn(Path.of("test/baseName"));
    when(c.absolutePath()).thenReturn(this.resourceDir.resolve("not-exists-file").toAbsolutePath());

    Publication d = mock();
    when(d.isPublished()).thenReturn(true);
    when(d.path()).thenReturn(Path.of("test/baseName"));
    when(d.absolutePath()).thenReturn(this.resourceDir.resolve("exists-file").toAbsolutePath());
    when(d.hash()).thenReturn("valid-hash");

    Publication e = mock();
    when(e.isPublished()).thenReturn(true);
    when(e.path()).thenReturn(Path.of("test/baseName"));
    when(e.absolutePath()).thenReturn(this.resourceDir.resolve("exists-file").toAbsolutePath());
    when(e.hash()).thenReturn("invalid-hash");

    when(directory.getPublications("baseName")).thenReturn(Arrays.asList(a, b, c, d, e));

    AnalyserResult result = this.analyser.analyse(ctx, path);

    AnalyserResult expected =
        ctx.getAnalyserResultFactory().createResult(ResultType.HASH_MISMATCH, e);

    assertEquals(expected, result, "Unexpected result");
  }

  @Test
  void testMediaMetaFileIsNotCompared() {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);
    when(path.baseName()).thenReturn("baseName");

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    Publication mediaMetaFile = mock();
    when(mediaMetaFile.isPublished()).thenReturn(true);
    when(mediaMetaFile.type()).thenReturn(PublicationType.OBJECT);
    when(mediaMetaFile.mediaId()).thenReturn(123L);
    when(mediaMetaFile.path()).thenReturn(Path.of("test/baseName"));
    when(mediaMetaFile.absolutePath())
        .thenReturn(this.resourceDir.resolve("exists-file").toAbsolutePath());
    when(mediaMetaFile.hash()).thenReturn("invalid-hash");

    when(directory.getPublications("baseName")).thenReturn(Arrays.asList(mediaMetaFile));

    assertEquals(
        AnalyserResult.OK,
        this.analyser.analyse(ctx, path),
        "a media meta file has no hash and should not be compared");
  }

  @Test
  void testMediumIsCompared() {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);
    when(path.baseName()).thenReturn("baseName");

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    Publication medium = mock();
    when(medium.isPublished()).thenReturn(true);
    when(medium.type()).thenReturn(PublicationType.PUBLIC_MEDIA);
    when(medium.mediaId()).thenReturn(123L);
    when(medium.path()).thenReturn(Path.of("test/baseName"));
    when(medium.absolutePath())
        .thenReturn(this.resourceDir.resolve("exists-file").toAbsolutePath());
    when(medium.hash()).thenReturn("invalid-hash");

    when(directory.getPublications("baseName")).thenReturn(Arrays.asList(medium));

    assertEquals(
        ctx.getAnalyserResultFactory().createResult(ResultType.HASH_MISMATCH, medium),
        this.analyser.analyse(ctx, path),
        "a medium with a media id should still be compared");
  }

  @SuppressWarnings("PMD.TestClassWithoutTestCases")
  private static final class TestHasher implements Hasher {
    @Override
    public String hash(Path file) {
      return "valid-hash";
    }
  }
}
