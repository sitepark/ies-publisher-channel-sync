package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
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
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class EmbeddedMediaDirectoryTest extends AnalyserTestBase {

  private final Path resourceDir =
      Path.of("src/test/resources/service/analyser/EmbeddedMediaDirectoryTest");

  private final EmbeddedMediaDirectory analyser = new EmbeddedMediaDirectory();

  @Test
  void testWhenPublishedPathIsNotADirectory() {
    AnalyserContext ctx = this.mockAnalyserContext();
    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testNoDotMediaSuffix() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.layout()).thenReturn(ChannelLayout.URL_BASED_RESOURCES);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("normal-directory");

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testInvalidParentPath() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.layout()).thenReturn(ChannelLayout.URL_BASED_RESOURCES);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("test.php.media");
    when(path.absolutePath()).thenReturn(Path.of("test.php.media"));

    assertThrows(IllegalArgumentException.class, () -> this.analyser.analyse(ctx, path));
  }

  @Test
  void testWithPublicationUrlBased() {
    AnalyserContext ctx = this.mockAnalyserContext();
    PublishedPath path = mock();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.layout()).thenReturn(ChannelLayout.URL_BASED_RESOURCES);

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
  void testWithPublicationIdBasedAndObjectOwner() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.layout()).thenReturn(ChannelLayout.ID_BASED_RESOURCES);

    Path mediaOwnerIdPath = Path.of("000/001/123");

    Path mediaOwnerPath =
        this.resourceDir.resolve(
            mediaOwnerIdPath.resolveSibling(mediaOwnerIdPath.getFileName() + ".php"));

    when(channel.pathFor(anyLong())).thenReturn(mediaOwnerIdPath);
    when(channel.resolve(
            PublicationType.OBJECT,
            mediaOwnerIdPath.resolveSibling(mediaOwnerIdPath.getFileName() + ".php")))
        .thenReturn(mediaOwnerPath);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("1234-123");
    when(path.absolutePath()).thenReturn(Path.of("/root/urlpath.media/1234-123"));

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    assertEquals(
        AnalyserResult.OK_AND_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK_AND_INTERRUPT");
  }

  @Test
  void testWithPublicationIdBasedAndMediaOwner() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.layout()).thenReturn(ChannelLayout.ID_BASED_RESOURCES);

    Path mediaOwnerIdPath = Path.of("000/001/345");

    Path mediaOwnerPath =
        this.resourceDir.resolve(
            mediaOwnerIdPath.resolveSibling(mediaOwnerIdPath.getFileName() + ".php"));

    when(channel.pathFor(anyLong())).thenReturn(mediaOwnerIdPath);
    when(channel.resolve(
            PublicationType.OBJECT,
            mediaOwnerIdPath.resolveSibling(mediaOwnerIdPath.getFileName() + ".php")))
        .thenReturn(mediaOwnerPath);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("1234-123");
    when(path.absolutePath()).thenReturn(this.resourceDir.resolve("urlpath.css.media/1234-123"));

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    assertEquals(
        AnalyserResult.OK_AND_INTERRUPT,
        this.analyser.analyse(ctx, path),
        "Should return OK_AND_INTERRUPT");
  }

  @Test
  void testIdBasedPathWithoutParent() {
    AnalyserContext ctx = this.mockIdBasedAnalyserContext();
    PublishedPath path = this.mediaOwnerDirectory("1234-123", Path.of("1234-123"));

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testIdBasedPathBelowFileSystemRoot() {
    AnalyserContext ctx = this.mockIdBasedAnalyserContext();
    PublishedPath path = this.mediaOwnerDirectory("1234-123", Path.of("/1234-123"));

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testIdBasedPathOutsideOfMediaDirectory() {
    AnalyserContext ctx = this.mockIdBasedAnalyserContext();
    PublishedPath path = this.mediaOwnerDirectory("1234-123", Path.of("/root/dir/1234-123"));

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testIdBasedMediaOwnerDirectoryWithoutMediaId() {
    AnalyserContext ctx = this.mockIdBasedAnalyserContext();
    PublishedPath path = this.mediaOwnerDirectory("1234", Path.of("/root/urlpath.media/1234"));

    assertEquals(
        this.deleteForce(ctx, path),
        this.analyser.analyse(ctx, path),
        "a directory not named <id>-<media id> should be deleted");
  }

  @Test
  void testIdBasedMediaOwnerDirectoryWithNonNumericId() {
    AnalyserContext ctx = this.mockIdBasedAnalyserContext();
    PublishedPath path =
        this.mediaOwnerDirectory("abc-123", Path.of("/root/urlpath.media/abc-123"));

    assertEquals(
        this.deleteForce(ctx, path),
        this.analyser.analyse(ctx, path),
        "a directory with a non numeric id should be deleted");
  }

  @Test
  void testIdBasedMediaOwnerDirectoryWithIdBeyondLayout() {
    AnalyserContext ctx = this.mockIdBasedAnalyserContext();
    when(ctx.getChannel().pathFor(anyLong())).thenThrow(new IllegalArgumentException("too big"));
    PublishedPath path =
        this.mediaOwnerDirectory("1000000000-123", Path.of("/root/urlpath.media/1000000000-123"));

    assertEquals(
        this.deleteForce(ctx, path),
        this.analyser.analyse(ctx, path),
        "a directory with an id that does not fit into the layout should be deleted");
  }

  @Test
  void testIdBasedMediaOwnerDirectoryWithoutAnyOwner() {
    AnalyserContext ctx = this.mockIdBasedAnalyserContext();
    this.withMissingObjectOwner(ctx);
    PublishedPath path =
        this.mediaOwnerDirectory(
            "1234-123", this.resourceDir.resolve("missing.css.media/1234-123"));

    assertEquals(
        this.deleteForce(ctx, path),
        this.analyser.analyse(ctx, path),
        "a directory whose owner neither exists as object nor as medium should be deleted");
  }

  @Test
  void testIdBasedMediaOwnerDirectoryWithoutAnyOwnerInRelativeMediaDirectory() {
    AnalyserContext ctx = this.mockIdBasedAnalyserContext();
    this.withMissingObjectOwner(ctx);
    PublishedPath path =
        this.mediaOwnerDirectory("1234-123", Path.of("missing.css.media/1234-123"));

    assertEquals(
        this.deleteForce(ctx, path),
        this.analyser.analyse(ctx, path),
        "a directory without a resolvable owner should be deleted");
  }

  private AnalyserContext mockIdBasedAnalyserContext() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.layout()).thenReturn(ChannelLayout.ID_BASED_RESOURCES);
    return ctx;
  }

  private void withMissingObjectOwner(AnalyserContext ctx) {
    Path mediaOwnerIdPath = Path.of("000/001/999");
    Path mediaOwnerFile = Path.of("000/001/999.php");
    when(ctx.getChannel().pathFor(anyLong())).thenReturn(mediaOwnerIdPath);
    when(ctx.getChannel().resolve(PublicationType.OBJECT, mediaOwnerFile))
        .thenReturn(this.resourceDir.resolve(mediaOwnerFile));
  }

  private PublishedPath mediaOwnerDirectory(String baseName, Path absolutePath) {
    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn(baseName);
    when(path.absolutePath()).thenReturn(absolutePath);
    return path;
  }

  private AnalyserResult deleteForce(AnalyserContext ctx, PublishedPath path) {
    return ctx.getAnalyserResultFactory()
        .createRecursiveInterruptResultDeleteForce(ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);
  }

  @Test
  void testWithoutPublication() {
    AnalyserContext ctx = this.mockAnalyserContext();
    PublishedPath path = mock();
    Channel channel = mock();
    when(ctx.getChannel()).thenReturn(channel);
    when(channel.layout()).thenReturn(ChannelLayout.URL_BASED_RESOURCES);

    when(path.isDirectory()).thenReturn(true);
    when(path.baseName()).thenReturn("test.php.media");
    when(path.absolutePath()).thenReturn(Path.of("/root/test.php.media"));

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    AnalyserResult result = this.analyser.analyse(ctx, path);

    AnalyserResult expected =
        ctx.getAnalyserResultFactory()
            .createRecursiveInterruptResultDeleteForce(ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);

    assertEquals(expected, result, "Unexpected result");
  }

  @Test
  void testPublicationWithoutMediaIdDir() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Publication publication = mock();
    when(publication.absolutePath()).thenReturn(Path.of("test.php"));

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, publication), "Should return OK");
  }

  @Test
  void testPublicationWithoutMediaDir() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Publication publication = mock();
    when(publication.absolutePath()).thenReturn(Path.of("b/test.php"));

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, publication), "Should return OK");
  }

  @Test
  void testPublicationWithoutMediaSuffixDir() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Publication publication = mock();
    when(publication.absolutePath()).thenReturn(Path.of("a/b/test.php"));

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, publication), "Should return OK");
  }

  @Test
  void testWithExistsPublicationUrlBased() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Publication publication = mock();
    when(publication.absolutePath())
        .thenReturn(this.resourceDir.resolve("test.php.media/123/image.png").toAbsolutePath());

    assertEquals(
        AnalyserResult.OK_AND_RECURSIVE_INTERRUPT,
        this.analyser.analyse(ctx, publication),
        "Should return OK_AND_INTERRUPT");
  }

  @Test
  void testWithExistsPublicationIdBased() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Publication publication = mock();
    when(publication.absolutePath())
        .thenReturn(this.resourceDir.resolve("test-id-based.media/123/image.png").toAbsolutePath());

    assertEquals(
        AnalyserResult.OK_AND_RECURSIVE_INTERRUPT,
        this.analyser.analyse(ctx, publication),
        "Should return OK_AND_INTERRUPT");
  }

  @Test
  void testWithNonExistsPublication() {
    AnalyserContext ctx = this.mockAnalyserContext();
    Publication publication = mock();
    when(publication.absolutePath())
        .thenReturn(
            this.resourceDir.resolve("test.php.media/123/non-exists-image.png").toAbsolutePath());

    AnalyserResult result = this.analyser.analyse(ctx, publication);

    AnalyserResult expected =
        ctx.getAnalyserResultFactory()
            .createRecursiveInterruptResult(ResultType.MISSING_FILE, publication);

    assertEquals(expected, result, "Unexpected result");
  }
}
