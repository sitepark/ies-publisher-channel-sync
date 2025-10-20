package com.sitepark.ies.publisher.channel.sync.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class AnalyserResultTest {

  @Test
  void testOk() {
    assertEquals(
        new AnalyserResult(Collections.emptyList(), false, false),
        AnalyserResult.OK,
        "Unexpected AnalyserResult");
  }

  @Test
  void testOkAndInterrupt() {
    assertEquals(
        new AnalyserResult(Collections.emptyList(), true, false),
        AnalyserResult.OK_AND_INTERRUPT,
        "Unexpected AnalyserResult");
  }

  @Test
  void testOkAndRecursiveInterrupt() {
    assertEquals(
        new AnalyserResult(Collections.emptyList(), true, true),
        AnalyserResult.OK_AND_RECURSIVE_INTERRUPT,
        "Unexpected AnalyserResult");
  }

  @Test
  void testUnmodifiedList() {

    ResultEntry a = mock();
    AnalyserResult result = new AnalyserResult(Collections.singletonList(a), false, false);

    ResultEntry b = mock();
    assertThrows(UnsupportedOperationException.class, () -> result.entries().add(b));
  }
}
