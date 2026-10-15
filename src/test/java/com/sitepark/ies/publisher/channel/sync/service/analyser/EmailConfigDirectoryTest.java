package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import org.junit.jupiter.api.Test;

class EmailConfigDirectoryTest extends AnalyserTestBase {

  private final EmailConfigDirectory analyser = new EmailConfigDirectory();

  @Test
  void testEmailConfigDirectoryIsSkipped() {
    AnalyserContext ctx = this.mockAnalyserContext();
    when(ctx.getPublicationType()).thenReturn(PublicationType.CONFIG);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("email");

    assertEquals(
        AnalyserResult.OK_AND_RECURSIVE_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "the email config directory should not be analysed further");
  }

  @Test
  void testOtherConfigDirectoryIsAnalysed() {
    AnalyserContext ctx = this.mockAnalyserContext();
    when(ctx.getPublicationType()).thenReturn(PublicationType.CONFIG);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("other");

    assertEquals(
        AnalyserResult.OK,
        this.analyser.analyse(ctx, path),
        "other config directories should be analysed further");
  }

  @Test
  void testEmailConfigFileIsAnalysed() {
    AnalyserContext ctx = this.mockAnalyserContext();
    when(ctx.getPublicationType()).thenReturn(PublicationType.CONFIG);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);

    assertEquals(
        AnalyserResult.OK,
        this.analyser.analyse(ctx, path),
        "a file named email should be analysed further");
  }

  @Test
  void testEmailObjectDirectoryIsAnalysed() {
    AnalyserContext ctx = this.mockAnalyserContext();
    when(ctx.getPublicationType()).thenReturn(PublicationType.OBJECT);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("email");

    assertEquals(
        AnalyserResult.OK,
        this.analyser.analyse(ctx, path),
        "an email directory outside the configs should be analysed further");
  }
}
