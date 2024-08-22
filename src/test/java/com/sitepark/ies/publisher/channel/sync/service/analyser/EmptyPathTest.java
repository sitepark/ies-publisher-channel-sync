package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class EmptyPathTest extends AnalyserTest {

  private final EmptyPath analyser = new EmptyPath();

  @Test
  void testWhenPublishedPathIsDirectory() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWithLostPublications() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);
    when(path.baseName()).thenReturn("baseName");

    Publication a = mock();
    when(a.isPublished()).thenReturn(false);
    Publication b = mock();
    when(b.isPublished()).thenReturn(true);
    when(b.path()).thenReturn(Path.of("test/baseName"));
    Publication c = mock();
    when(c.isPublished()).thenReturn(true);
    when(c.path()).thenReturn(Path.of(""));
    Publication d = mock();
    when(d.isPublished()).thenReturn(true);
    when(d.path()).thenReturn(Path.of(""));

    when(directory.getPublications("baseName")).thenReturn(Arrays.asList(a, b, c, d));

    AnalyserResult result = this.analyser.analyse(ctx, path);

    List<ResultEntry> expectedResultEntires = new ArrayList<ResultEntry>();
    expectedResultEntires.add(
        ctx.getResultEntryFactory().createResultEntry(ResultType.LOST_PUBLICATION, c));
    expectedResultEntires.add(
        ctx.getResultEntryFactory().createResultEntry(ResultType.ILLEGAL_FILENAME_COLLISION, d));

    AnalyserResult expected = ctx.getAnalyserResultFactory().createResult(expectedResultEntires);

    assertEquals(expected, result, "Unexpected result");
  }
}
