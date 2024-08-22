package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserContext;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class FileDirectoryMismatchTest extends AnalyserTest {

  private FileDirectoryMismatch analyser = new FileDirectoryMismatch();

  @Test
  void testWhenPublishedPathIsDirectory() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWithoutMatchedPublicationDirectory() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);

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

    PublicationDirectory child = mock();
    when(directory.getChild("baseName")).thenReturn(child);

    AnalyserResult result = this.analyser.analyse(ctx, path);

    AnalyserResult expected =
        ctx.getAnalyserResultFactory()
            .createInterruptResult(ResultType.FILE_DIRECTORY_MISMATCH, path);

    assertEquals(expected, result, "Unexpected result");
  }
}
