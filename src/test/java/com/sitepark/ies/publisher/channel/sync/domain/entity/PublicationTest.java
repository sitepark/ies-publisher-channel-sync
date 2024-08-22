package com.sitepark.ies.publisher.channel.sync.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import java.nio.file.Path;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.TooManyMethods")
class PublicationTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    EqualsVerifier.forClass(Publication.class).verify();
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  public void testToString() {
    ToStringVerifier.forClass(Publication.class).withClassName(NameStyle.SIMPLE_NAME).verify();
  }

  @Test
  void testType() {
    PublicationType type = mock();
    Path path = Path.of("a");
    Publication publication = Publication.builder().type(type).path(path).build();
    assertEquals(type, publication.type(), "unexpected type");
  }

  @Test
  void testMissingType() {
    Path path = Path.of("a");
    assertThrows(
        NullPointerException.class, () -> Publication.builder().path(path).build(), "missing type");
  }

  @Test
  void testPath() {
    PublicationType type = mock();
    Path path = Path.of("a");
    Publication publication = Publication.builder().type(type).path(path).build();
    assertEquals(path, publication.path(), "unexpected path");
  }

  @Test
  void testPahtWithAbsolutePath() {
    PublicationType type = mock();
    Path path = Path.of("/a");
    Publication publication = Publication.builder().type(type).path(path).build();
    assertEquals(Path.of("a"), publication.path(), "unexpected path");
  }

  @Test
  void testMissingPath() {
    PublicationType type = mock();
    assertThrows(
        NullPointerException.class, () -> Publication.builder().type(type).build(), "missing path");
  }

  @Test
  void testNullFileName() {
    PublicationType type = mock();
    Path path = Path.of("/");
    assertThrows(
        IllegalArgumentException.class,
        () -> Publication.builder().type(type).path(path),
        "path without filename should not allowed");
  }

  @Test
  void testBlankPath() {
    PublicationType type = mock();
    Path path = Path.of("");
    Publication publication = Publication.builder().type(type).path(path).build();
    assertEquals(Path.of(""), publication.path(), "unexpected path");
  }

  @Test
  void testFileName() {
    PublicationType type = mock();
    Path path = Path.of("a/b");
    Publication publication = Publication.builder().type(type).path(path).build();
    assertEquals("b", publication.fileName(), "unexpected fileName");
  }

  @Test
  void testObject() {
    PublicationType type = mock();
    Path path = Path.of("a");
    Ref object = mock();
    Publication publication = Publication.builder().type(type).path(path).object(object).build();
    assertEquals(object, publication.object(), "unexpected object");
  }

  @Test
  void testHash() {
    PublicationType type = mock();
    Path path = Path.of("a");
    Publication publication = Publication.builder().type(type).path(path).hash("abc").build();
    assertEquals("abc", publication.hash(), "unexpected hash");
  }

  @Test
  void testIsPublished() {
    PublicationType type = mock();
    Path path = Path.of("a");
    Publication publication = Publication.builder().type(type).path(path).isPublished().build();
    assertEquals(true, publication.isPublished(), "unexpected isPublished");
  }

  @Test
  void testIsPublishedWithParam() {
    PublicationType type = mock();
    Path path = Path.of("a");
    Publication publication = Publication.builder().type(type).path(path).isPublished(true).build();
    assertEquals(true, publication.isPublished(), "unexpected isPublished");
  }

  @Test
  void testCollidesWith() {
    PublicationType type = mock();
    Path path = Path.of("a");
    Ref collidesWith = mock();
    Publication publication =
        Publication.builder().type(type).path(path).collidesWith(collidesWith).build();
    assertEquals(collidesWith, publication.collidesWith(), "unexpected collidesWith");
  }

  @Test
  void testIsCollisionTrue() {
    PublicationType type = mock();
    Path path = Path.of("a");
    Ref collidesWith = mock();
    Publication publication =
        Publication.builder().type(type).path(path).collidesWith(collidesWith).build();
    assertTrue(publication.isCollision(), "unexpected isCollision");
  }

  @Test
  void testIsCollisionFalse() {
    PublicationType type = mock();
    Path path = Path.of("a");
    Publication publication = Publication.builder().type(type).path(path).build();
    assertFalse(publication.isCollision(), "unexpected isCollision");
  }

  @Test
  void testAbsolutePath() {
    PublicationType type = mock();
    Path path = Path.of("a");
    Path absolutePath = mock();
    Publication publication =
        Publication.builder().type(type).path(path).absolutePath(absolutePath).build();
    assertEquals(absolutePath, publication.absolutePath(), "unexpected absolutePath");
  }
}
