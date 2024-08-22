package com.sitepark.ies.publisher.channel.sync.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ResultTypeTest {

  @Test
  void testGetSymbol() {
    assertEquals('?', ResultType.UNKNOWN_FILE_OR_DIRECTORY.getSymbol(), "unexpected symbol");
  }

  @Test
  void testGetDescription() {
    assertEquals(
        "Unknown file or directory",
        ResultType.UNKNOWN_FILE_OR_DIRECTORY.getDescription(),
        "unexpected description");
  }
}
