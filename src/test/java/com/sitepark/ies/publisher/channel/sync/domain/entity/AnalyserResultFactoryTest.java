package com.sitepark.ies.publisher.channel.sync.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class AnalyserResultFactoryTest {

  private Publication publication;

  private PublishedPath path;

  private ResultEntryFactory entryFactory;

  private AnalyserResultFactory factory;

  @BeforeEach
  void setup() {
    this.entryFactory = mock();
    this.publication = mock();
    this.path = mock();
    this.factory = new AnalyserResultFactory(entryFactory);
  }

  @Test
  void testCreateResultWithPublication() {

    ResultEntry entry = mock();
    when(entryFactory.createResultEntry(any(ResultType.class), any(Publication.class)))
        .thenReturn(entry);

    AnalyserResult result = this.factory.createResult(ResultType.MISSING_FILE, this.publication);
    AnalyserResult expected = new AnalyserResult(Arrays.asList(entry), false);
    assertEquals(expected, result, "unexpected result");
  }

  @Test
  void testCreateResultWithPublishedPath() {

    ResultEntry entry = mock();
    when(entryFactory.createResultEntry(any(ResultType.class), any(PublishedPath.class)))
        .thenReturn(entry);

    AnalyserResult result = this.factory.createResult(ResultType.MISSING_FILE, this.path);
    AnalyserResult expected = new AnalyserResult(Arrays.asList(entry), false);
    assertEquals(expected, result, "unexpected result");
  }

  @Test
  void createInterruptResultWithPublication() {

    ResultEntry entry = mock();
    when(entryFactory.createResultEntry(any(ResultType.class), any(Publication.class)))
        .thenReturn(entry);

    AnalyserResult result =
        this.factory.createInterruptResult(ResultType.MISSING_FILE, this.publication);
    AnalyserResult expected = new AnalyserResult(Arrays.asList(entry), true);
    assertEquals(expected, result, "unexpected result");
  }

  @Test
  void createInterruptResultWithPublishedPath() {

    ResultEntry entry = mock();
    when(entryFactory.createResultEntry(any(ResultType.class), any(PublishedPath.class)))
        .thenReturn(entry);

    AnalyserResult result = this.factory.createInterruptResult(ResultType.MISSING_FILE, this.path);
    AnalyserResult expected = new AnalyserResult(Arrays.asList(entry), true);
    assertEquals(expected, result, "unexpected result");
  }

  @Test
  void testCreateInterruptResultDeleteForceWithPublishedPath() {

    ResultEntry entry = mock();
    when(entryFactory.createResultEntryDeleteForce(any(ResultType.class), any(PublishedPath.class)))
        .thenReturn(entry);

    AnalyserResult result =
        this.factory.createInterruptResultDeleteForce(ResultType.MISSING_FILE, this.path);
    AnalyserResult expected = new AnalyserResult(Arrays.asList(entry), true);
    assertEquals(expected, result, "unexpected result");
  }

  @Test
  void testCreateResultWithResultEntryList() {

    ResultEntry entry = mock();

    AnalyserResult result = this.factory.createResult(Arrays.asList(entry));
    AnalyserResult expected = new AnalyserResult(Arrays.asList(entry), false);
    assertEquals(expected, result, "unexpected result");
  }

  @Test
  void testCreateInterruptResultWithResultEntryList() {

    ResultEntry entry = mock();

    AnalyserResult result = this.factory.createInterruptResult(Arrays.asList(entry));
    AnalyserResult expected = new AnalyserResult(Arrays.asList(entry), true);
    assertEquals(expected, result, "unexpected result");
  }
}
