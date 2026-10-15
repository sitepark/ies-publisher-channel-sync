package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;
import com.sitepark.ies.publisher.channel.sync.service.Channel;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class UnknownDirectoryTest extends AnalyserTestBase {

  private final UnknownDirectory analyser = new UnknownDirectory();

  @Test
  void testFileIsIgnored() {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "a file should be ignored");
  }

  @Test
  void testNonRecursiveAnalysisIgnoresDirectories() {
    AnalyserContext ctx = this.mockAnalyserContext();
    when(ctx.isRecursive()).thenReturn(false);

    PublishedPath path = this.directory("dir");

    assertEquals(
        AnalyserResult.OK,
        this.analyser.analyse(ctx, path),
        "a non recursive analysis should not report directories");
  }

  @Test
  void testDirectoryWithoutPublicationsIsUnknown() {
    AnalyserContext ctx = this.mockAnalyserContext();
    when(ctx.isRecursive()).thenReturn(true);

    PublishedPath path = this.directory("dir");

    assertEquals(
        ctx.getAnalyserResultFactory().createResult(ResultType.UNKNOWN_FILE_OR_DIRECTORY, path),
        this.analyser.analyse(ctx, path),
        "a directory without publications should be unknown");
  }

  @Test
  void testUrlBasedDirectoryWithPublicationsIsKnown() {
    AnalyserContext ctx = this.mockAnalyserContext(ChannelLayout.URL_BASED_RESOURCES);
    PublishedPath path = this.directory("dir");
    this.childWith(ctx, "dir", PublicationType.CONFIG);

    assertEquals(
        AnalyserResult.OK,
        this.analyser.analyse(ctx, path),
        "a url based directory with publications should be known");
  }

  @Test
  void testIdBasedDirectoryWithPublicationOfSameTypeIsKnown() {
    AnalyserContext ctx = this.mockAnalyserContext(ChannelLayout.ID_BASED_RESOURCES);
    PublishedPath path = this.directory("000");
    this.childWith(ctx, "000", PublicationType.CONFIG, PublicationType.OBJECT);

    assertEquals(
        AnalyserResult.OK,
        this.analyser.analyse(ctx, path),
        "an id based directory with a publication of the analysed type should be known");
  }

  @Test
  void testIdBasedDirectoryWithPublicationsOfOtherTypeIsUnknown() {
    AnalyserContext ctx = this.mockAnalyserContext(ChannelLayout.ID_BASED_RESOURCES);
    PublishedPath path = this.directory("000");
    this.childWith(ctx, "000", PublicationType.CONFIG);

    assertEquals(
        ctx.getAnalyserResultFactory().createResult(ResultType.UNKNOWN_FILE_OR_DIRECTORY, path),
        this.analyser.analyse(ctx, path),
        "an id based directory only holding publications of another type should be unknown");
  }

  private AnalyserContext mockAnalyserContext(ChannelLayout layout) {
    AnalyserContext ctx = this.mockAnalyserContext();
    when(ctx.isRecursive()).thenReturn(true);
    when(ctx.getPublicationType()).thenReturn(PublicationType.OBJECT);
    Channel channel = mock();
    when(channel.layout()).thenReturn(layout);
    when(ctx.getChannel()).thenReturn(channel);
    return ctx;
  }

  private PublishedPath directory(String baseName) {
    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn(baseName);
    return path;
  }

  private void childWith(AnalyserContext ctx, String name, PublicationType... types) {
    List<Publication> publications =
        Arrays.stream(types)
            .map(
                type -> {
                  Publication publication = mock();
                  when(publication.type()).thenReturn(type);
                  return publication;
                })
            .toList();
    PublicationDirectory child = mock();
    when(child.getPublications(true)).thenReturn(publications);
    when(ctx.getPublicationDirectory().getChild(name)).thenReturn(child);
  }
}
