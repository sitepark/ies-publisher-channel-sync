package com.sitepark.ies.publisher.channel.sync.domain.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import java.nio.file.Path;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
class PublicationDirectoryTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    PublicationDirectory b = PublicationDirectory.builder().name("b").build();
    PublicationDirectory a = PublicationDirectory.builder().name("a").child(b).build();
    EqualsVerifier.forClass(PublicationDirectory.class)
        .withPrefabValues(PublicationDirectory.class, a, b)
        .withIgnoredFields("parent")
        .verify();
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testToString() {
    PublicationDirectory a = PublicationDirectory.builder().name("a").build();
    ToStringVerifier.forClass(PublicationDirectory.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPrefabValue(PublicationDirectory.class, a)
        .verify();
  }

  @Test
  void testName() {
    PublicationDirectory directory = PublicationDirectory.builder().name("a").build();
    assertEquals("a", directory.getName(), "unexpected name");
  }

  @Test
  void testNullName() {
    PublicationDirectory directory = PublicationDirectory.builder().name(null).build();
    assertNull(directory.getName(), "null should be allowed");
  }

  @Test
  void testBlankName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> PublicationDirectory.builder().name(""),
        "name should not be blank");
  }

  @Test
  void testNameStartsWithSlash() {
    assertThrows(
        IllegalArgumentException.class,
        () -> PublicationDirectory.builder().name("/abc"),
        "name should not start with /");
  }

  @Test
  void testNameEndsWithSlash() {
    assertThrows(
        IllegalArgumentException.class,
        () -> PublicationDirectory.builder().name("abc/"),
        "name should not end with /");
  }

  @Test
  void testNameEndsNull() {
    assertThrows(
        IllegalArgumentException.class,
        () -> PublicationDirectory.builder().name("abc/").build(),
        "name should not end with /");
  }

  @Test
  void testGetParent() {
    PublicationDirectory child = PublicationDirectory.builder().build();
    PublicationDirectory parent = PublicationDirectory.builder().child(child).build();
    assertEquals(parent, child.getParent(), "Unexpected parent");
  }

  @Test
  void testGetPublications() {

    Publication publication = mock();
    when(publication.fileName()).thenReturn("fileName");
    PublicationDirectory directory =
        PublicationDirectory.builder().publication(publication).build();
    assertThat(
        "Unexpected publications",
        directory.getPublications(false),
        containsInAnyOrder(publication));
  }

  @Test
  void testGetPublicationsRecursive() {

    Publication b = mock();
    when(b.fileName()).thenReturn("b");
    PublicationDirectory child = PublicationDirectory.builder().publication(b).build();

    Publication a = mock();
    when(a.fileName()).thenReturn("a");
    PublicationDirectory directory =
        PublicationDirectory.builder().publication(a).child(child).build();
    assertThat(
        "Unexpected publications", directory.getPublications(true), containsInAnyOrder(a, b));
  }

  @Test
  void testGetPublicationFileNames() {

    Publication publication = mock();
    when(publication.fileName()).thenReturn("fileName");
    PublicationDirectory directory =
        PublicationDirectory.builder().publication(publication).build();
    assertThat(
        "Unexpected publications",
        directory.getPublicationFileNames(),
        containsInAnyOrder("fileName"));
  }

  @Test
  void testGetPublicationFileNamesWithoutPublications() {
    PublicationDirectory directory = PublicationDirectory.builder().build();
    assertThat("Unexpected publications", directory.getPublicationFileNames(), empty());
  }

  @Test
  void testGetPublicationsByFileNames() {
    Publication publication = mock();
    when(publication.fileName()).thenReturn("fileName");
    PublicationDirectory directory =
        PublicationDirectory.builder().publication(publication).build();
    assertThat(
        "Unexpected publications",
        directory.getPublications("fileName"),
        containsInAnyOrder(publication));
  }

  @Test
  void testGetPublicationsByFileNamesWithoutPublications() {
    PublicationDirectory directory = PublicationDirectory.builder().build();
    assertThat("Unexpected publications", directory.getPublications("fileName"), empty());
  }

  @Test
  void testHasPublication() {
    Publication publication = mock();
    when(publication.fileName()).thenReturn("fileName");
    PublicationDirectory directory =
        PublicationDirectory.builder().publication(publication).build();
    assertTrue(directory.hasPublications("fileName"), "should have publication");
  }

  @Test
  void testHasPublicationWithoutPublications() {
    PublicationDirectory directory = PublicationDirectory.builder().build();
    assertFalse(directory.hasPublications("fileName"), "should not have publication");
  }

  @Test
  void testPublications() {
    Publication publication = mock();
    when(publication.fileName()).thenReturn("fileName");
    PublicationDirectory directory =
        PublicationDirectory.builder().publications(List.of(publication)).build();
    assertThat(
        "Unexpected publications",
        directory.getPublications("fileName"),
        containsInAnyOrder(publication));
  }

  @Test
  void testGetChildrenSetByChildren() {
    PublicationDirectory child = mock();
    when(child.getName()).thenReturn("name");
    PublicationDirectory directory =
        PublicationDirectory.builder().children(List.of(child)).build();
    assertThat("Unexpected children", directory.getChildren(), containsInAnyOrder(child));
  }

  @Test
  void testGetChildrenSetByChild() {
    PublicationDirectory child = mock();
    when(child.getName()).thenReturn("name");
    PublicationDirectory directory = PublicationDirectory.builder().child(child).build();
    assertThat("Unexpected children", directory.getChildren(), containsInAnyOrder(child));
  }

  @Test
  void testGetChild() {
    PublicationDirectory child = mock();
    when(child.getName()).thenReturn("name");
    PublicationDirectory directory = PublicationDirectory.builder().child(child).build();
    assertEquals(directory.getChild("name"), child, "Unexpected child");
  }

  @Test
  void testFindChild() {
    PublicationDirectory b = mock();
    when(b.getName()).thenReturn("b");
    PublicationDirectory a = mock();
    when(a.getName()).thenReturn("a");
    when(a.getChild(anyString())).thenReturn(b);
    PublicationDirectory directory = PublicationDirectory.builder().child(a).build();
    assertEquals(directory.findChild(Path.of("a/b")), b, "Unexpected child");
  }

  @Test
  void testFindChildNotFound() {
    PublicationDirectory a = mock();
    when(a.getName()).thenReturn("a");
    PublicationDirectory directory = PublicationDirectory.builder().child(a).build();
    assertNull(directory.findChild(Path.of("a/b")), "Child should not be found");
  }

  @Test
  void testGetCollision() {
    Publication publication = mock();
    when(publication.isCollision()).thenReturn(true);
    Ref object = new Ref("123");
    when(publication.object()).thenReturn(object);

    PublicationDirectory directory =
        PublicationDirectory.builder().publication(publication).build();

    assertEquals(publication, directory.getCollision("_123"), "Unexpected collision");
  }
}
