package com.sitepark.ies.publisher.channel.sync.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.service.analyser.AnalyserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResultEntryFactoryTest {

  private PublicationDirectory publicationDirectory;

  private ResultEntryFactory factory;

  @BeforeEach
  void setup() {
    AnalyserContext ctx = mock();
    this.publicationDirectory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(this.publicationDirectory);
    this.factory = new ResultEntryFactory(ctx);
  }

  @Test
  void testCreateResultEntryWithPublication() {

    Publication publication = mock();
    ResultEntry entry =
        this.factory.createResultEntry(ResultType.FILE_DIRECTORY_MISMATCH, publication);

    ResultEntry expected =
        ResultEntry.builder()
            .resultType(ResultType.FILE_DIRECTORY_MISMATCH)
            .publication(publication)
            .publicationDirectory(this.publicationDirectory)
            .build();

    assertEquals(expected, entry, "unexpected entry");
  }

  @Test
  void testCreateResultEntryWithPublishedPath() {

    PublishedPath path = mock();
    ResultEntry entry = this.factory.createResultEntry(ResultType.FILE_DIRECTORY_MISMATCH, path);

    ResultEntry expected =
        ResultEntry.builder()
            .resultType(ResultType.FILE_DIRECTORY_MISMATCH)
            .publishedPath(path)
            .publicationDirectory(this.publicationDirectory)
            .build();

    assertEquals(expected, entry, "unexpected entry");
  }

  @Test
  void testCcreateResultEntryDeleteForceWithPublishedPath() {

    PublishedPath path = mock();
    ResultEntry entry =
        this.factory.createResultEntryDeleteForce(ResultType.FILE_DIRECTORY_MISMATCH, path);

    ResultEntry expected =
        ResultEntry.builder()
            .resultType(ResultType.FILE_DIRECTORY_MISMATCH)
            .publishedPath(path)
            .publicationDirectory(this.publicationDirectory)
            .deleteForce(true)
            .build();

    assertEquals(expected, entry, "unexpected entry");
  }
}
