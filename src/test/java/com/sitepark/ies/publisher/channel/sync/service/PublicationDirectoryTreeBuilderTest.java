package com.sitepark.ies.publisher.channel.sync.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Ref;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PublicationDirectoryTreeBuilderTest {

  @Test
  void testBuild() {

    PublicationDirectoryTreeBuilder builder = new PublicationDirectoryTreeBuilder();

    Publication a = this.createPublication("a");
    Publication b = this.createPublication("b");
    Publication d = this.createPublication("c/d");
    Publication e = this.createPublication("c/e");
    Publication g = this.createPublication("c/f/g");

    builder.add(a);
    builder.add(b);
    builder.add(d);
    builder.add(e);
    builder.add(g);

    PublicationDirectory expected =
        PublicationDirectory.builder()
            .publication(a)
            .publication(b)
            .child(
                PublicationDirectory.builder()
                    .name("c")
                    .publication(d)
                    .publication(e)
                    .child(PublicationDirectory.builder().name("f").publication(g).build())
                    .build())
            .build();

    PublicationDirectory directory = builder.build();

    assertEquals(expected, directory, "The directory tree should be equal");
  }

  @Test
  void testBuildWithCollision() {

    Ref collistionWith = new Ref("123");
    Ref object = new Ref("456");

    Publication b =
        Publication.builder()
            .type(PublicationType.OBJECT)
            .path(Path.of("a/_456/b"))
            .object(object)
            .collidesWith(collistionWith)
            .build();

    PublicationDirectoryTreeBuilder builder = new PublicationDirectoryTreeBuilder();
    builder.add(b);

    PublicationDirectory expected =
        PublicationDirectory.builder()
            .child(PublicationDirectory.builder().name("a").publication(b).build())
            .build();

    PublicationDirectory directory = builder.build();

    assertEquals(expected, directory, "The directory tree should be equal");
  }

  @Test
  void testBuildWithCollisionWithoutParent() {

    Ref collistionWith = new Ref("123");
    Ref object = new Ref("456");

    Publication b =
        Publication.builder()
            .type(PublicationType.OBJECT)
            .path(Path.of("a"))
            .object(object)
            .collidesWith(collistionWith)
            .build();

    PublicationDirectoryTreeBuilder builder = new PublicationDirectoryTreeBuilder();
    builder.add(b);

    PublicationDirectory expected = PublicationDirectory.builder().publication(b).build();

    PublicationDirectory directory = builder.build();

    assertEquals(expected, directory, "The directory tree should be equal");
  }

  @Test
  void testBuildWithCollisionWithInvalidCollistionDirectoryName() {

    Ref collistionWith = new Ref("123");
    Ref object = new Ref("456");

    Publication b =
        Publication.builder()
            .type(PublicationType.OBJECT)
            .path(Path.of("a/_abc/b"))
            .object(object)
            .collidesWith(collistionWith)
            .build();

    PublicationDirectoryTreeBuilder builder = new PublicationDirectoryTreeBuilder();
    builder.add(b);

    PublicationDirectory expected =
        PublicationDirectory.builder()
            .child(
                PublicationDirectory.builder()
                    .name("a")
                    .child(PublicationDirectory.builder().name("_abc").publication(b).build())
                    .build())
            .build();

    PublicationDirectory directory = builder.build();

    assertEquals(expected, directory, "The directory tree should be equal");
  }

  @Test
  void testBuildWithCollisionInBaseDirectory() {

    Ref collistionWith = new Ref("123");
    Ref object = new Ref("456");

    Publication b =
        Publication.builder()
            .type(PublicationType.OBJECT)
            .path(Path.of("_456/b"))
            .object(object)
            .collidesWith(collistionWith)
            .build();

    PublicationDirectoryTreeBuilder builder = new PublicationDirectoryTreeBuilder();
    builder.add(b);

    PublicationDirectory expected = PublicationDirectory.builder().publication(b).build();

    PublicationDirectory directory = builder.build();

    assertEquals(expected, directory, "The directory tree should be equal");
  }

  private Publication createPublication(String path) {

    Path p = Path.of(path);

    return Publication.builder().type(PublicationType.OBJECT).path(p).build();
  }
}
