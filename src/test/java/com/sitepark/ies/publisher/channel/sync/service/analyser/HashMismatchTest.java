package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import com.sitepark.ies.publisher.channel.sync.port.Hasher;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class HashMismatchTest extends AnalyserTest {

  private final HashMismatch analyser = new HashMismatch(new TestHasher());

  private final Path resourceDir = Path.of("src/test/resources/service/analyser/HashMismatchTest");

  @Test
  void testWhenPublishedPathIsDirectory() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWithMatchedPublicationDirectory() throws IOException {
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

  @SuppressWarnings("PMD.TestClassWithoutTestCases")
  private static final class TestHasher implements Hasher {
    @Override
    public String hash(Path file) throws IOException {
      return "valid-hash";
    }
  }
}
