package com.sitepark.ies.publisher.channel.sync.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import java.util.Collections;
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
    AnalyserResult expected = new AnalyserResult(Collections.singletonList(entry), false);
    assertEquals(expected, result, "unexpected result");
  }

  @Test
  void testCreateResultWithPublishedPath() {

    ResultEntry entry = mock();
    when(entryFactory.createResultEntry(any(ResultType.class), any(PublishedPath.class)))
        .thenReturn(entry);

    AnalyserResult result = this.factory.createResult(ResultType.MISSING_FILE, this.path);
    AnalyserResult expected = new AnalyserResult(Collections.singletonList(entry), false);
    assertEquals(expected, result, "unexpected result");
  }

  @Test
  void createInterruptResultWithPublication() {

    ResultEntry entry = mock();
    when(entryFactory.createResultEntry(any(ResultType.class), any(Publication.class)))
        .thenReturn(entry);

    AnalyserResult result =
        this.factory.createInterruptResult(ResultType.MISSING_FILE, this.publication);
    AnalyserResult expected = new AnalyserResult(Collections.singletonList(entry), true);
    assertEquals(expected, result, "unexpected result");
  }

  @Test
  void createInterruptResultWithPublishedPath() {

    ResultEntry entry = mock();
    when(entryFactory.createResultEntry(any(ResultType.class), any(PublishedPath.class)))
        .thenReturn(entry);

    AnalyserResult result = this.factory.createInterruptResult(ResultType.MISSING_FILE, this.path);
    AnalyserResult expected = new AnalyserResult(Collections.singletonList(entry), true);
    assertEquals(expected, result, "unexpected result");
  }

  @Test
  void testCreateInterruptResultDeleteForceWithPublishedPath() {

    ResultEntry entry = mock();
    when(entryFactory.createResultEntryDeleteForce(any(ResultType.class), any(PublishedPath.class)))
        .thenReturn(entry);

    AnalyserResult result =
        this.factory.createInterruptResultDeleteForce(ResultType.MISSING_FILE, this.path);
    AnalyserResult expected = new AnalyserResult(Collections.singletonList(entry), true);
    assertEquals(expected, result, "unexpected result");
  }

  @Test
  void testCreateResultWithResultEntryList() {

    ResultEntry entry = mock();

    AnalyserResult result = this.factory.createResult(Collections.singletonList(entry));
    AnalyserResult expected = new AnalyserResult(Collections.singletonList(entry), false);
    assertEquals(expected, result, "unexpected result");
  }

  @Test
  void testCreateInterruptResultWithResultEntryList() {

    ResultEntry entry = mock();

    AnalyserResult result = this.factory.createInterruptResult(Collections.singletonList(entry));
    AnalyserResult expected = new AnalyserResult(Collections.singletonList(entry), true);
    assertEquals(expected, result, "unexpected result");
  }
}
