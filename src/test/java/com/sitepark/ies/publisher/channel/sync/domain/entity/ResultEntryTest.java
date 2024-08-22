package com.sitepark.ies.publisher.channel.sync.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import java.nio.file.Path;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.TooManyMethods")
class ResultEntryTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    PublicationDirectory a = PublicationDirectory.builder().name("a").build();
    PublicationDirectory b = PublicationDirectory.builder().name("b").build();
    EqualsVerifier.forClass(ResultEntry.class)
        .withPrefabValues(PublicationDirectory.class, a, b)
        .verify();
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  public void testToString() {
    ToStringVerifier.forClass(ResultEntry.class).withClassName(NameStyle.SIMPLE_NAME).verify();
  }

  @Test
  void testGetResultType() {
    ResultEntry resultEntry =
        ResultEntry.builder().resultType(ResultType.FILE_DIRECTORY_MISMATCH).build();
    assertEquals(
        ResultType.FILE_DIRECTORY_MISMATCH, resultEntry.getResultType(), "unexpected type");
  }

  @Test
  void testGetPublicationDirectory() {
    PublicationDirectory directory = mock();
    ResultEntry resultEntry = ResultEntry.builder().publicationDirectory(directory).build();
    assertEquals(directory, resultEntry.getPublicationDirectory(), "unexpected directory");
  }

  @Test
  void testGetPublication() {
    Publication publication = mock();
    ResultEntry resultEntry = ResultEntry.builder().publication(publication).build();
    assertEquals(publication, resultEntry.getPublication(), "unexpected publication");
  }

  @Test
  void testGetPublishedPath() {
    PublishedPath publishedPath = mock();
    ResultEntry resultEntry = ResultEntry.builder().publishedPath(publishedPath).build();
    assertEquals(publishedPath, resultEntry.getPublishedPath(), "unexpected path");
  }

  @Test
  void testIsDeleteForce() {
    ResultEntry resultEntry = ResultEntry.builder().deleteForce(true).build();
    assertEquals(true, resultEntry.isDeleteForce(), "unexpected delete force");
  }

  @Test
  void testIsTemporary() {
    ResultEntry resultEntry = ResultEntry.builder().temporary(true).build();
    assertEquals(true, resultEntry.isTemporary(), "unexpected temporary");
  }

  @Test
  void testGetAbsolutePathFromPublication() {
    Path absolutePath = Path.of("/test");
    Publication publication = mock();
    when(publication.absolutePath()).thenReturn(absolutePath);

    ResultEntry resultEntry = ResultEntry.builder().publication(publication).build();
    assertEquals(absolutePath, resultEntry.getAbsolutePath(), "unexpected path");
  }

  @Test
  void testGetAbsolutePathFromPublishPath() {
    Path absolutePath = Path.of("/test");
    PublishedPath publishedPath = mock();
    when(publishedPath.absolutePath()).thenReturn(absolutePath);

    ResultEntry resultEntry = ResultEntry.builder().publishedPath(publishedPath).build();
    assertEquals(absolutePath, resultEntry.getAbsolutePath(), "unexpected path");
  }

  @Test
  void testGetObject() {
    Ref object = new Ref("123");
    Publication publication = mock();
    when(publication.object()).thenReturn(object);

    ResultEntry resultEntry = ResultEntry.builder().publication(publication).build();
    assertEquals(object, resultEntry.getObject(), "unexpected object");
  }

  @Test
  void testGetObjectWithoutPublication() {
    ResultEntry resultEntry = ResultEntry.builder().build();
    assertNull(resultEntry.getObject(), "object should be null");
  }

  @Test
  void testGetNameFromPublication() {
    Publication publication = mock();
    when(publication.fileName()).thenReturn("a");
    PublishedPath publishedPath = mock();
    when(publishedPath.baseName()).thenReturn("b");
    PublicationDirectory directory = mock();
    when(directory.getName()).thenReturn("c");

    ResultEntry resultEntry =
        ResultEntry.builder()
            .publicationDirectory(directory)
            .publishedPath(publishedPath)
            .publication(publication)
            .build();

    assertEquals("a", resultEntry.getName(), "unexpected name");
  }

  @Test
  void testGetNameFromPublishedPath() {
    PublishedPath publishedPath = mock();
    when(publishedPath.baseName()).thenReturn("b");
    PublicationDirectory directory = mock();
    when(directory.getName()).thenReturn("c");

    ResultEntry resultEntry =
        ResultEntry.builder().publicationDirectory(directory).publishedPath(publishedPath).build();

    assertEquals("b", resultEntry.getName(), "unexpected name");
  }

  @Test
  void testGetNameFromPublicationDirectory() {
    PublicationDirectory directory = mock();
    when(directory.getName()).thenReturn("c");
    ResultEntry resultEntry = ResultEntry.builder().publicationDirectory(directory).build();

    assertEquals("c", resultEntry.getName(), "unexpected name");
  }
}
