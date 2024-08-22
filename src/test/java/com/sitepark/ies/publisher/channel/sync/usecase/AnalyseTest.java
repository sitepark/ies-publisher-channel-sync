package com.sitepark.ies.publisher.channel.sync.usecase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Ref;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import com.sitepark.ies.publisher.channel.sync.port.Filter;
import com.sitepark.ies.publisher.channel.sync.port.Hasher;
import com.sitepark.ies.publisher.channel.sync.port.Publisher;
import com.sitepark.ies.publisher.channel.sync.service.Channel;
import com.sitepark.ies.publisher.channel.sync.service.PublicationDirectoryTreeBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
class AnalyseTest {

  private final Path root = Path.of("src/test/resources/usecase/AnalyseTest").toAbsolutePath();

  private static final Filter FILTER =
      new Filter() {
        @Override
        @SuppressWarnings("PMD.LiteralsFirstInComparisons")
        public boolean accept(PublishedPath path) {
          String name = path.baseName();
          return name.indexOf("ignore") == -1 && !name.equals(".gitkeep");
        }
      };

  private static final Hasher HASHER =
      new Hasher() {
        @Override
        public String hash(Path path) {
          return "hash";
        }
      };

  @Test
  void testWithEmptyRootDir() throws IOException {

    AnalyserBuilder analyseBuilder =
        analyseBuilder()
            .root(this.root.resolve("testWithEmptyRootDir"))
            .layout(ChannelLayout.DOCUMENT_ROOT);

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    assertEquals(Arrays.asList(), result, "Should return an empty list");
  }

  @Test
  void testWithAbsolutePath() throws IOException {

    AnalyserBuilder analyseBuilder =
        analyseBuilder()
            .root(this.root.resolve("testWithEmptyRootDir"))
            .layout(ChannelLayout.DOCUMENT_ROOT);

    Analyse analyse = analyseBuilder.build();

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          analyse.analyse(Path.of("/"), true);
        });
  }

  @Test
  void testWithEmptyDirectoryUnkownDir() throws IOException {

    Path testRoot = this.root.resolve("testWithEmptyDirectoryUnkownDir");

    AnalyserBuilder analyseBuilder =
        analyseBuilder().root(testRoot).layout(ChannelLayout.DOCUMENT_ROOT);

    PublicationDirectory publicationDirectory = analyseBuilder.buildPublicationDirectory();

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    ResultEntry expected =
        ResultEntry.builder()
            .resultType(ResultType.UNKNOWN_FILE_OR_DIRECTORY)
            .publicationDirectory(publicationDirectory)
            .publishedPath(this.createPublishedPath(testRoot, "empty-directory"))
            .build();

    assertEquals(Arrays.asList(expected), result, "Should return a missing file");
  }

  @Test
  void testWithEmptyDirectoryMissingFile() throws IOException {

    AnalyserBuilder analyseBuilder =
        analyseBuilder()
            .root(this.root.resolve("testWithEmptyDirectoryMissingFile"))
            .layout(ChannelLayout.DOCUMENT_ROOT)
            .publication("empty-directory-missing-publication/f");

    PublicationDirectory publicationDirectory = analyseBuilder.buildPublicationDirectory();
    PublicationDirectory emptyDirectoryMissingPublication =
        publicationDirectory.getChild("empty-directory-missing-publication");
    Publication f = emptyDirectoryMissingPublication.getPublications("f").get(0);

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    ResultEntry expected =
        ResultEntry.builder()
            .resultType(ResultType.MISSING_FILE)
            .publicationDirectory(emptyDirectoryMissingPublication)
            .publication(f)
            .build();

    assertEquals(Arrays.asList(expected), result, "Should return a missing file");
  }

  @Test
  void testFilter() throws IOException {

    AnalyserBuilder analyseBuilder =
        analyseBuilder()
            .root(this.root.resolve("testFilter"))
            .layout(ChannelLayout.DOCUMENT_ROOT)
            .publication("a");

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    assertEquals(Collections.emptyList(), result, "Should return a empty list");
  }

  @Test
  void testWithAppendageSuffixDir() throws IOException {

    AnalyserBuilder analyseBuilder =
        analyseBuilder()
            .root(this.root.resolve("testWithAppendageSuffixDir"))
            .layout(ChannelLayout.DOCUMENT_ROOT)
            .publication("a");

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    assertEquals(Collections.emptyList(), result, "Should return a empty list");
  }

  @Test
  void testWithAppendageSuffixDirWithoutCorrespondingFile() throws IOException {

    Path testRoot = this.root.resolve("testWithAppendageSuffixDirWithoutCorrespondingFile");
    AnalyserBuilder analyseBuilder =
        analyseBuilder().root(testRoot).layout(ChannelLayout.DOCUMENT_ROOT);

    PublicationDirectory publicationDirectory = analyseBuilder.buildPublicationDirectory();

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    ResultEntry expected =
        ResultEntry.builder()
            .resultType(ResultType.UNKNOWN_FILE_OR_DIRECTORY)
            .publicationDirectory(publicationDirectory)
            .publishedPath(this.createPublishedPath(testRoot, "a.scaled"))
            .deleteForce(true)
            .build();

    assertEquals(Arrays.asList(expected), result, "Should return a unknown directory");
  }

  @Test
  void testWithCollisionDir() throws IOException {

    Ref object = new Ref("123");
    Ref collidesWith = new Ref("345");

    AnalyserBuilder analyseBuilder =
        analyseBuilder()
            .root(this.root.resolve("testWithCollisionDir"))
            .layout(ChannelLayout.DOCUMENT_ROOT);
    analyseBuilder.publication(
        analyseBuilder.publicationBuilder("a/b").object(collidesWith).build());
    analyseBuilder.publication(
        analyseBuilder
            .publicationBuilder("a/_123/b")
            .object(object)
            .collidesWith(collidesWith)
            .build());

    PublicationDirectory publicationDirectory = analyseBuilder.buildPublicationDirectory();
    PublicationDirectory a = publicationDirectory.getChild("a");

    Publication b = a.getPublications("b").get(1);

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    ResultEntry expected =
        ResultEntry.builder()
            .resultType(ResultType.LEGAL_FILENAME_COLLISION)
            .publicationDirectory(a)
            .publication(b)
            .build();

    assertEquals(Arrays.asList(expected), result, "Should return a legal filename collision");
  }

  @Test
  void testWithCollisionDirAndMissingCollisionFile() throws IOException {

    Ref object = new Ref("123");
    Ref collidesWith = new Ref("345");

    AnalyserBuilder analyseBuilder =
        analyseBuilder()
            .root(this.root.resolve("testWithCollisionDirAndMissingCollisionFile"))
            .layout(ChannelLayout.DOCUMENT_ROOT);
    analyseBuilder.publication(
        analyseBuilder
            .publicationBuilder("_123/a")
            .object(object)
            .collidesWith(collidesWith)
            .build());

    PublicationDirectory publicationDirectory = analyseBuilder.buildPublicationDirectory();
    Publication a = publicationDirectory.getPublications("a").get(0);

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    ResultEntry expected =
        ResultEntry.builder()
            .resultType(ResultType.MISSING_FILE)
            .publicationDirectory(publicationDirectory)
            .publication(a)
            .build();

    assertEquals(Arrays.asList(expected), result, "Should return a missing file entry");
  }

  @Test
  void testFileDirectoryMismatch() throws IOException {

    Path testRoot = this.root.resolve("testFileDirectoryMismatch");

    AnalyserBuilder analyseBuilder =
        analyseBuilder().root(testRoot).layout(ChannelLayout.DOCUMENT_ROOT).publication("a");

    PublicationDirectory publicationDirectory = analyseBuilder.buildPublicationDirectory();
    Publication a = publicationDirectory.getPublications("a").get(0);

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    ResultEntry expectedMismatch =
        ResultEntry.builder()
            .resultType(ResultType.FILE_DIRECTORY_MISMATCH)
            .publicationDirectory(publicationDirectory)
            .publication(a)
            .build();
    ResultEntry expectedUnknown =
        ResultEntry.builder()
            .resultType(ResultType.UNKNOWN_FILE_OR_DIRECTORY)
            .publicationDirectory(publicationDirectory)
            .publishedPath(this.createPublishedPath(testRoot, "a"))
            .build();

    assertEquals(
        Arrays.asList(expectedMismatch, expectedUnknown),
        result,
        "Should return a file directory mismatch");
  }

  @Test
  void testDirectoryFileMismatch() throws IOException {

    Path testRoot = this.root.resolve("testDirectoryFileMismatch");
    AnalyserBuilder analyseBuilder =
        analyseBuilder().root(testRoot).layout(ChannelLayout.DOCUMENT_ROOT).publication("a/b");

    PublicationDirectory publicationDirectory = analyseBuilder.buildPublicationDirectory();
    PublicationDirectory a = publicationDirectory.getChild("a");
    Publication b = a.getPublications("b").get(0);

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    ResultEntry expectedMismatch =
        ResultEntry.builder()
            .resultType(ResultType.FILE_DIRECTORY_MISMATCH)
            .publicationDirectory(publicationDirectory)
            .publishedPath(this.createPublishedPath(testRoot, "a"))
            .build();
    ResultEntry missingFile =
        ResultEntry.builder()
            .resultType(ResultType.MISSING_FILE)
            .publicationDirectory(a)
            .publication(b)
            .build();

    assertEquals(
        Arrays.asList(expectedMismatch, missingFile),
        result,
        "Should return a file directory mismatch and a missing file entry");
  }

  @Test
  void testHashMismatch() throws IOException {

    AnalyserBuilder analyseBuilder =
        analyseBuilder()
            .root(this.root.resolve("testHashMismatch"))
            .layout(ChannelLayout.DOCUMENT_ROOT);
    analyseBuilder.publication(analyseBuilder.publicationBuilder("a").hash("other").build());

    PublicationDirectory publicationDirectory = analyseBuilder.buildPublicationDirectory();
    Publication a = publicationDirectory.getPublications("a").get(0);

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    ResultEntry expected =
        ResultEntry.builder()
            .resultType(ResultType.HASH_MISMATCH)
            .publicationDirectory(publicationDirectory)
            .publication(a)
            .build();

    assertEquals(Arrays.asList(expected), result, "Should return a hash-mismatch entry");
  }

  @Test
  void testUnpublished() throws IOException {

    Path testRoot = this.root.resolve("testUnpublished");

    AnalyserBuilder analyseBuilder =
        analyseBuilder()
            .root(this.root.resolve("testUnpublished"))
            .layout(ChannelLayout.DOCUMENT_ROOT);
    analyseBuilder.publication(analyseBuilder.publicationBuilder("a").isPublished(false).build());

    PublicationDirectory publicationDirectory = analyseBuilder.buildPublicationDirectory();

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    ResultEntry expected1 =
        ResultEntry.builder()
            .resultType(ResultType.UNKNOWN_FILE_OR_DIRECTORY)
            .publicationDirectory(publicationDirectory)
            .publishedPath(this.createPublishedPath(testRoot, "a"))
            .build();

    ResultEntry expected2 =
        ResultEntry.builder()
            .resultType(ResultType.UNKNOWN_FILE_OR_DIRECTORY)
            .publicationDirectory(publicationDirectory)
            .publishedPath(this.createPublishedPath(testRoot, "a.scaled"))
            .deleteForce(true)
            .build();

    assertThat(
        "Should return a unknown file or directory entry",
        result,
        containsInAnyOrder(expected1, expected2));
  }

  @Test
  void testPublicationsWithEmptyPathWithoutFile() throws IOException {

    AnalyserBuilder analyseBuilder =
        analyseBuilder()
            .root(this.root.resolve("testPublicationsWithEmptyPathWithoutFile"))
            .layout(ChannelLayout.DOCUMENT_ROOT)
            .publication("a")
            .publication("");

    PublicationDirectory publicationDirectory = analyseBuilder.buildPublicationDirectory();
    Publication b = publicationDirectory.getPublications("").get(0);

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    ResultEntry expectedLostPublication =
        ResultEntry.builder()
            .resultType(ResultType.LOST_PUBLICATION)
            .publicationDirectory(publicationDirectory)
            .publication(b)
            .build();

    assertEquals(
        Arrays.asList(expectedLostPublication),
        result,
        "Should return a lost publicationand and illegal collistion entry");
  }

  @Test
  void testPublicationsWithEmptyPathWithFile() throws IOException {

    Path testRoot = this.root.resolve("testPublicationsWithEmptyPathWithFile");

    AnalyserBuilder analyseBuilder =
        analyseBuilder()
            .root(this.root.resolve("testPublicationsWithEmptyPathWithFile"))
            .layout(ChannelLayout.DOCUMENT_ROOT)
            .publication("")
            .publication("");

    PublicationDirectory publicationDirectory = analyseBuilder.buildPublicationDirectory();
    Publication a1 = publicationDirectory.getPublications("").get(0);
    Publication a2 = publicationDirectory.getPublications("").get(1);

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    ResultEntry unknownFile =
        ResultEntry.builder()
            .resultType(ResultType.UNKNOWN_FILE_OR_DIRECTORY)
            .publicationDirectory(publicationDirectory)
            .publishedPath(this.createPublishedPath(testRoot, "a"))
            .build();

    ResultEntry expectedLostPublication1 =
        ResultEntry.builder()
            .resultType(ResultType.LOST_PUBLICATION)
            .publicationDirectory(publicationDirectory)
            .publication(a1)
            .build();

    ResultEntry expectedLostPublication2 =
        ResultEntry.builder()
            .resultType(ResultType.LOST_PUBLICATION)
            .publicationDirectory(publicationDirectory)
            .publication(a2)
            .build();

    assertEquals(
        Arrays.asList(unknownFile, expectedLostPublication1, expectedLostPublication2),
        result,
        "Should return a lost publication entry");
  }

  @Test
  void testMissingPublication() throws IOException {

    AnalyserBuilder analyseBuilder =
        analyseBuilder()
            .root(this.root.resolve("testMissingPublication"))
            .layout(ChannelLayout.DOCUMENT_ROOT)
            .publication("a")
            .publication("b");

    PublicationDirectory publicationDirectory = analyseBuilder.buildPublicationDirectory();
    Publication b = publicationDirectory.getPublications("b").get(0);

    List<ResultEntry> result = this.analyseRecursive(analyseBuilder.build());

    ResultEntry expectedMissingFile =
        ResultEntry.builder()
            .resultType(ResultType.MISSING_FILE)
            .publicationDirectory(publicationDirectory)
            .publication(b)
            .build();

    assertEquals(
        Arrays.asList(expectedMissingFile),
        result,
        "Should return a missing file and collision entry");
  }

  @Test
  void testNonRecursive() throws IOException {

    AnalyserBuilder analyseBuilder =
        analyseBuilder()
            .root(this.root.resolve("testNonRecursive"))
            .layout(ChannelLayout.DOCUMENT_ROOT)
            .publication("a")
            .publication("b")
            .publication("c/d");

    PublicationDirectory publicationDirectory = analyseBuilder.buildPublicationDirectory();
    Publication b = publicationDirectory.getPublications("b").get(0);

    List<ResultEntry> result = this.analyseNonRecursive(analyseBuilder.build());

    ResultEntry expectedTemplateMissing =
        ResultEntry.builder()
            .resultType(ResultType.MISSING_FILE)
            .publicationDirectory(publicationDirectory)
            .publication(b)
            .build();

    assertEquals(
        Arrays.asList(expectedTemplateMissing), result, "Should return a template missing entry");
  }

  @Test
  void testWithDocumentRootLayout() throws IOException {

    AnalyserBuilder builder =
        analyseBuilder()
            .root(this.root.resolve("testWithDocumentRootLayout"))
            .layout(ChannelLayout.DOCUMENT_ROOT);

    this.buildPublicationTestTree(builder);

    Analyse analyse = builder.build();

    PublicationDirectory publicationDirectory = builder.buildPublicationDirectory();
    PublicationDirectory a = publicationDirectory.getChild("a");
    PublicationDirectory b = a.getChild("b");
    Publication objectPhp = b.getPublications("object.php").get(1);
    Publication missingPhp = b.getPublications("missing.php").get(0);
    PublicationDirectory mediaId2 = b.getChild("object.php.media").getChild("media-id2");
    Publication missingEmbeddedImage =
        mediaId2.getPublications("missing-embedded-image.png").get(0);

    List<ResultEntry> result = this.analyseRecursive(analyse, Path.of("a"));

    ResultEntry expectedLegalFilenameCollision =
        ResultEntry.builder()
            .resultType(ResultType.LEGAL_FILENAME_COLLISION)
            .publicationDirectory(b)
            .publication(objectPhp)
            .build();

    ResultEntry expectedMissingFile =
        ResultEntry.builder()
            .resultType(ResultType.MISSING_FILE)
            .publicationDirectory(b)
            .publication(missingPhp)
            .build();

    ResultEntry expectedMissingEmbeddedImage =
        ResultEntry.builder()
            .resultType(ResultType.MISSING_FILE)
            .publicationDirectory(mediaId2)
            .publication(missingEmbeddedImage)
            .build();

    assertEquals(
        Arrays.asList(
            expectedLegalFilenameCollision, expectedMissingFile, expectedMissingEmbeddedImage),
        result,
        "Should return a legal filename collision, a missing file and a missing embedded image");
  }

  @Test
  void testWithResourcesLayout() throws IOException {

    AnalyserBuilder builder =
        analyseBuilder()
            .root(this.root.resolve("testWithResourcesLayout"))
            .layout(ChannelLayout.RESOURCES);

    this.buildPublicationTestTree(builder);

    Analyse analyse = builder.build();

    PublicationDirectory publicationDirectory = builder.buildPublicationDirectory();
    PublicationDirectory a = publicationDirectory.getChild("a");
    PublicationDirectory b = a.getChild("b");
    Publication objectPhp = b.getPublications("object.php").get(1);
    Publication missingPhp = b.getPublications("missing.php").get(0);
    PublicationDirectory mediaId2 = b.getChild("object.php.media").getChild("media-id2");
    Publication missingEmbeddedImage =
        mediaId2.getPublications("missing-embedded-image.png").get(0);

    List<ResultEntry> result = this.analyseRecursive(analyse, Path.of("a"));

    ResultEntry expectedLegalFilenameCollision =
        ResultEntry.builder()
            .resultType(ResultType.LEGAL_FILENAME_COLLISION)
            .publicationDirectory(b)
            .publication(objectPhp)
            .build();

    ResultEntry expectedMissingFile =
        ResultEntry.builder()
            .resultType(ResultType.MISSING_FILE)
            .publicationDirectory(b)
            .publication(missingPhp)
            .build();

    ResultEntry expectedMissingEmbeddedImage =
        ResultEntry.builder()
            .resultType(ResultType.MISSING_FILE)
            .publicationDirectory(mediaId2)
            .publication(missingEmbeddedImage)
            .build();

    assertEquals(
        Arrays.asList(
            expectedLegalFilenameCollision, expectedMissingFile, expectedMissingEmbeddedImage),
        result,
        "Should return a legal filename collision, a missing file and a missing embedded image");
  }

  private void buildPublicationTestTree(AnalyserBuilder builder) {

    Ref object = new Ref("123");
    Ref collidesWith = new Ref("345");

    builder.publication(
        builder
            .publicationBuilder("a/b/object.php")
            .type(PublicationType.OBJECT)
            .object(collidesWith)
            .build());
    builder.publication(
        PublicationType.PUBLIC_MEDIA, "a/b/object.php.media/media-id/embedded-image.png");
    builder.publication(
        PublicationType.PUBLIC_MEDIA, "a/b/object.php.media/media-id2/missing-embedded-image.png");
    builder.publication(PublicationType.OBJECT, "a/b/protected-object.php");
    builder.publication(
        PublicationType.PROTECTED_MEDIA,
        "a/b/protected-object.php.media/media-id/embedded-protected-image.png");
    builder.publication(PublicationType.PUBLIC_MEDIA, "a/b/image.png");
    builder.publication(PublicationType.PROTECTED_MEDIA, "a/b/protected-image.png");
    builder.publication(PublicationType.CONFIG, "a/b/config.php");
    builder.publication(PublicationType.OBJECT, "a/b/missing.php");

    builder.publication(
        builder
            .publicationBuilder("a/b/_123/object.php")
            .type(PublicationType.OBJECT)
            .object(object)
            .collidesWith(collidesWith)
            .build());
  }

  private List<ResultEntry> analyseRecursive(Analyse analyse) throws IOException {
    return this.analyseRecursive(analyse, Path.of(""));
  }

  private List<ResultEntry> analyseRecursive(Analyse analyse, Path path) throws IOException {
    return this.analyseRecursive(analyse, path, true);
  }

  private List<ResultEntry> analyseNonRecursive(Analyse analyse) throws IOException {
    return this.analyseRecursive(analyse, Path.of(""), false);
  }

  @SuppressWarnings("PMD.SystemPrintln")
  private List<ResultEntry> analyseRecursive(Analyse analyse, Path path, boolean recursive)
      throws IOException {
    AnalyserResult result = analyse.analyse(path, recursive);

    boolean debug = true;
    if (debug) {
      result.entries().stream()
          .forEach(
              pe -> {
                PublicationType type = null;
                if (pe.getPublication() != null) {
                  type = pe.getPublication().type();
                } else if (pe.getPublishedPath() != null) {
                  type = pe.getPublishedPath().type();
                }
                System.out.println(
                    pe.getResultType() + " " + pe.getAbsolutePath() + " (" + type + ")");
              });
    }
    return result.entries();
  }

  private PublishedPath createPublishedPath(Path testRoot, String p) {
    Path path = Path.of(p);
    Path pathFileName = path.getFileName();
    if (pathFileName == null) {
      throw new IllegalArgumentException("Path must have a file name");
    }
    return new PublishedPath(
        PublicationType.OBJECT, testRoot.resolve(path).toAbsolutePath(), pathFileName.toString());
  }

  private AnalyserBuilder analyseBuilder() {
    return new AnalyserBuilder();
  }

  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  private static final class AnalyserBuilder {

    private Path root;

    private ChannelLayout layout;

    private Channel channel;

    private final List<Publication> publications = new ArrayList<>();

    public AnalyserBuilder layout(ChannelLayout layout) {
      this.layout = layout;
      return this;
    }

    public AnalyserBuilder root(Path root) {
      this.root = root;
      return this;
    }

    public AnalyserBuilder publication(String path) {
      return this.publication(PublicationType.OBJECT, path);
    }

    public AnalyserBuilder publication(PublicationType type, String path) {
      return this.publication(this.publicationBuilder(type, path).build());
    }

    public AnalyserBuilder publication(Publication publication) {
      this.publications.add(publication);
      return this;
    }

    public Publication.Builder publicationBuilder(String path) {
      return this.publicationBuilder(PublicationType.OBJECT, path);
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public Publication.Builder publicationBuilder(PublicationType type, String path) {
      Path p = Path.of(path);
      return Publication.builder()
          .type(type)
          .absolutePath(this.getChannel().resolve(type, path).toAbsolutePath())
          .path(p)
          .isPublished()
          .hash("hash");
    }

    private Channel getChannel() {
      if (this.channel != null) {
        return this.channel;
      }
      if (this.layout == null) {
        throw new IllegalStateException("layout must be set");
      }
      if (this.root == null) {
        throw new IllegalStateException("root must be set");
      }
      this.channel = new Channel(this.layout, this.root);
      return this.channel;
    }

    public Analyse build() {
      Publisher publisher = mock();
      when(publisher.getPublications(any())).thenReturn(this.publications);
      return new Analyse(this.getChannel(), publisher, FILTER, HASHER);
    }

    public PublicationDirectory buildPublicationDirectory() {
      PublicationDirectoryTreeBuilder builder = new PublicationDirectoryTreeBuilder();
      for (Publication publication : this.publications) {
        builder.add(publication);
      }
      return builder.build();
    }
  }
}
