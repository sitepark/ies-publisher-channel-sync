package com.sitepark.ies.publisher.channel.sync.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class AnalyserResultTest {

  @Test
  void testOk() {
    assertEquals(
        new AnalyserResult(Collections.emptyList(), false),
        AnalyserResult.OK,
        "Unexpected AnalyserResult");
  }

  @Test
  void testOkAndInterrupt() {
    assertEquals(
        new AnalyserResult(Collections.emptyList(), true),
        AnalyserResult.OK_AND_INTERRUPT,
        "Unexpected AnalyserResult");
  }

  @Test
  void testUnmodifiedList() {

    ResultEntry a = mock();
    AnalyserResult result = new AnalyserResult(Collections.singletonList(a), false);

    ResultEntry b = mock();
    assertThrows(UnsupportedOperationException.class, () -> result.entries().add(b));
  }
}
